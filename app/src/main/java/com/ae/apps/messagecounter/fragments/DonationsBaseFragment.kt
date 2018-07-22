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

    protected abstract fun handlePurchaseError(purchases: List<Purchase>?)

    protected abstract fun handleUserCancelled(purchases: List<Purchase>?)

    /**
     * Compare with "skuDetails.getSku()" to sku name and fetch the corresponding
     * price associated with it as "skuDetails.getPrice()"
     *
     * @param skuDetails list of SKUs
     */
    protected abstract fun skuDetailsResponse(skuDetails: List<SkuDetails>)

    /**
     * A successful purchase
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
            handlePurchaseError(purchases)
        }
    }

}
