/*
 * Copyright 2018 Midhun Harikumar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ae.apps.messagecounter.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import com.android.billingclient.api.*

/**
 * Google Play Billing Library based on
 * https://developer.android.com/google/play/billing/billing_library_overview#java
 *
 * Use below in build.gradle for dependencies
 * implementation 'com.android.billingclient:billing:1.1'
 */
abstract class DonationsBaseFragment : Fragment(), PurchasesUpdatedListener {

    private var mBillingClient: BillingClient? = null

    /**
     * Invoked when the BillingClient has setup correctly
     */
    protected abstract fun onBillingClientSetup()

    protected abstract fun getSkus(): List<String>

    protected abstract fun handlePurchaseError(purchases: List<Purchase>?, responseCode: Int)

    protected abstract fun handleUserCancelled(purchases: List<Purchase>?)

    /**
     * Compare with "skuDetails.getSku()" to sku name and fetch the corresponding
     * price associated with it as "skuDetails.getPrice()"
     *
     * @param skuDetails list of SKUs
     */
    protected abstract fun skuDetailsResponse(skuDetails: List<SkuDetails>)

    /**
     * Method invoked on a successful purchase
     *
     * @param purchase purchase details
     */
    protected abstract fun handlePurchase(purchase: Purchase)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBillingClient = BillingClient
                .newBuilder(requireActivity())
                .setListener(this)
                .build()

        startServiceConnection()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (null != mBillingClient) {
            mBillingClient!!.endConnection()
        }
    }

    /**
     * Launch the billing flow
     *
     * @param skuId id of the sku
     */
    protected fun launchBillingFlow(skuId: String) {
        val flowParams = BillingFlowParams.newBuilder()
                .setSku(skuId)
                .setType(BillingClient.SkuType.INAPP)
                .build()
        mBillingClient!!.launchBillingFlow(requireActivity(), flowParams)
    }

    private fun startServiceConnection() {
        mBillingClient!!.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(responseCode: Int) {
                if (responseCode == BillingClient.BillingResponse.OK) {
                    // Billing client is ready
                    onBillingClientSetup()

                    querySkuDetails()
                }
            }

            override fun onBillingServiceDisconnected() {}
        })
    }

    private fun querySkuDetails() {
        val params = SkuDetailsParams.newBuilder()
                .setSkusList(getSkus())
                .setType(BillingClient.SkuType.INAPP)
                .build()

        mBillingClient!!.querySkuDetailsAsync(params) { responseCode, skuDetailsList ->
            if (responseCode == BillingClient.BillingResponse.OK) {
                skuDetailsResponse(skuDetailsList)
            }
        }
    }

    /**
     * Consume the Purchase so that it could be bought again
     *
     * @param purchaseToken the purchase token
     * @param listener a callback when the purchase is consumed
     */
    fun consumeAsync(purchaseToken:String, listener:ConsumeResponseListener){
        mBillingClient!!.consumeAsync(purchaseToken, listener)
    }

    override fun onPurchasesUpdated(responseCode: Int, purchases: List<Purchase>?) {
        if (responseCode == BillingClient.BillingResponse.OK && null != purchases) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (responseCode == BillingClient.BillingResponse.USER_CANCELED) {
            // Handle user cancelled case
            handleUserCancelled(purchases)
        } else {
            // Handle an error flow
            handlePurchaseError(purchases, responseCode)
        }
    }

}
