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
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ae.apps.messagecounter.R
import com.ae.apps.messagecounter.data.preferences.PreferenceRepository
import com.ae.apps.messagecounter.databinding.FragmentDonationsBinding
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeResponseListener
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails

class DonationsFragment : DonationsBaseFragment(R.layout.fragment_donations), ConsumeResponseListener {

    companion object {
        const val SKU_SMALL = "product_small2"
        const val SKU_MEDIUM = "product_medium2"
        const val SKU_LARGE = "product_large2"

        fun newInstance() = DonationsFragment()
    }

    private lateinit var binding:FragmentDonationsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentDonationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun getSkus(): List<String> {
        return mutableListOf(SKU_SMALL, SKU_MEDIUM, SKU_LARGE)
    }

    override fun onBillingClientSetup() {
//        loadingProgressBar?.visibility = View.GONE
    }

    override fun skuDetailsResponse(skuDetails: List<SkuDetails>) {
        /*
        skuDetails.forEach {
            when (it.sku) {
                SKU_SMALL -> txtDonateSmallPrice.text = it.price
                SKU_MEDIUM -> txtDonateMediumPrice.text = it.price
                SKU_LARGE -> txtDonateLargePrice.text = it.price
            }
        }
         */
    }

    override fun handlePurchase(purchase: Purchase) {
        // Consume the Purchase so that it can be bought again
        consumeAsync(purchase.purchaseToken, this)

        // FIXME
    /*
            Toast.makeText(baseContext,
                "Thank You for your donation, order id " + purchase.orderId,
                Toast.LENGTH_SHORT).show()
     */
        val preferenceRepository = PreferenceRepository.newInstance(
                PreferenceManager.getDefaultSharedPreferences(requireContext()))
        preferenceRepository.saveDonationsMade()
    }

    override fun onPurchasesUpdated(p0: BillingResult, p1: MutableList<Purchase>?) {
        TODO("Not yet implemented")
    }

    fun onConsumeResponse(responseCode: Int, purchaseToken: String?) {
        /*Toast.makeText(requireContext(),
                "Purchase Confirmed",
                Toast.LENGTH_SHORT).show()
                */
    }

    override fun handlePurchaseError(purchases: List<Purchase>?, responseCode: Int) {
        /*
                Toast.makeText(requireContext(),
                "Purchase error occurred. Code $responseCode",
                Toast.LENGTH_SHORT)
                .show()
         */
    }

    override fun handleUserCancelled(purchases: List<Purchase>?) {

    }

    private fun initUI() {
        binding.btnDonateSmall.setOnClickListener {
            launchBillingFlow(SKU_SMALL)
        }
        binding.btnDonateMedium.setOnClickListener {
            launchBillingFlow(SKU_MEDIUM)
        }
        binding.btnDonateLarge.setOnClickListener {
            launchBillingFlow(SKU_LARGE)
        }
    }

    override fun onConsumeResponse(p0: BillingResult, p1: String) {
        TODO("Not yet implemented")
    }
}