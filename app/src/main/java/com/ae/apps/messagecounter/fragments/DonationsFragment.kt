package com.ae.apps.messagecounter.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ae.apps.messagecounter.BuildConfig
import com.ae.apps.messagecounter.R
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import kotlinx.android.synthetic.main.fragment_donations.*

class DonationsFragment : DonationsBaseFragment() {

    companion object {
        fun newInstance() = DonationsFragment()
    }

    private val SKU_SMALL = "product_small"
    private val SKU_MEDIUM = "product_medium"
    private val SKU_LARGE = "product_large"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_donations, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initUI()
    }

    override fun getSkus(): List<String> {
        return mutableListOf(SKU_SMALL, SKU_MEDIUM, SKU_LARGE)
    }

    override fun onBillingClientSetup() {
        loadingProgressBar.visibility = View.GONE
    }

    override fun skuDetailsResponse(skuDetails: List<SkuDetails>) {
        Toast.makeText(requireContext(),
                "skuDetailsResponse size:" + skuDetails.size,
                Toast.LENGTH_SHORT)
                .show()
        skuDetails.forEach {
            when {
                it.sku == SKU_SMALL -> txtDonateSmallPrice.text = it.price
                it.sku == SKU_MEDIUM -> txtDonateMediumPrice.text = it.price
                it.sku == SKU_LARGE -> txtDonateLargePrice.text = it.price
            }
        }
    }

    override fun handlePurchase(purchase: Purchase) {
        Toast.makeText(requireContext(),
                "Thank You for your donation with order id" + purchase.orderId,
                Toast.LENGTH_SHORT)
                .show()
    }

    override fun handlePurchaseError(purchases: List<Purchase>?) {
        Toast.makeText(requireContext(),
                "purchase error occured",
                Toast.LENGTH_SHORT)
                .show()
    }

    override fun handleUserCancelled(purchases: List<Purchase>?) {

    }

    private fun initUI() {
        btnDonateSmall.setOnClickListener {
            launchBillingFlow(SKU_SMALL)
        }
        btnDonateMedium.setOnClickListener {
            launchBillingFlow(SKU_MEDIUM)
        }
        btnDonateLarge.setOnClickListener {
            launchBillingFlow(SKU_LARGE)
        }
    }
}