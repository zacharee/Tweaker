package com.zacharee1.systemuituner.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode.BILLING_UNAVAILABLE
import com.android.billingclient.api.BillingClient.BillingResponseCode.OK
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.dialogs.DonateDialog
import kotlinx.coroutines.*


class BillingUtil(private val dialog: DonateDialog) : CoroutineScope by MainScope() {
    private val client: BillingClient

    init {
        client = BillingClient.newBuilder(dialog.context).setListener { response, purchases ->
            if (response.responseCode == OK && purchases != null) {
                for (purchase in purchases) {
                    consumeAsync(purchase.purchaseToken)
                }
            }
        }.enablePendingPurchases().build()

        client.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == OK) {
                    val ppTitle = dialog.view.findViewById<TextView>(R.id.paypal_title)
                    val ppButton = dialog.view.findViewById<Button>(R.id.paypal_button)

                    if (ppTitle != null) ppTitle.visibility = View.GONE
                    if (ppButton != null) ppButton.visibility = View.GONE
                } else if (result.responseCode == BILLING_UNAVAILABLE) {
                    val gPlayD = dialog.view.findViewById<LinearLayout>(R.id.google_play_donate)
                    val gPlayDT = dialog.view.findViewById<TextView>(R.id.google_play_donate_title)

                    if (gPlayD != null) {
                        gPlayD.visibility = View.GONE
                    }

                    if (gPlayDT != null) {
                        gPlayDT.visibility = View.GONE
                    }
                }
            }

            override fun onBillingServiceDisconnected() {}
        })
    }

    private fun consumeAsync(token: String) {
        client.consumeAsync(ConsumeParams.newBuilder().setPurchaseToken(token).build()) { _, _ -> }
    }

    fun doDonate(sku: String) = launch {
        val skus = arrayListOf(sku)

        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skus).setType(BillingClient.SkuType.INAPP)
        val result = withContext(Dispatchers.IO) {
            client.querySkuDetails(params.build())
        }

        val list = result.skuDetailsList
        if (result.billingResult.responseCode == OK && list != null && list.isNotEmpty()) {
            client.launchBillingFlow(dialog.context as Activity, BillingFlowParams.newBuilder().setSkuDetails(list[0]).build())
        }
    }

    companion object {
        fun onDonatePayPalClicked(context: Context) {
            context.launchUrl("https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=zachary.wander@gmail.com")
        }
    }
}