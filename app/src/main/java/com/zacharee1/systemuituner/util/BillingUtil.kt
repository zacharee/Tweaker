package com.zacharee1.systemuituner.util

import android.app.Activity
import android.content.Context
import android.view.ContextThemeWrapper
import androidx.core.view.isVisible
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode.OK
import com.zacharee1.systemuituner.databinding.LayoutDonateBinding
import com.zacharee1.systemuituner.dialogs.DonateDialog
import kotlinx.coroutines.*


class BillingUtil(private val dialog: DonateDialog) : CoroutineScope by MainScope() {
    private val client: BillingClient

    private val activity: Activity = dialog.context.extractActivity()

    init {
        client = BillingClient.newBuilder(dialog.context).setListener { response, purchases ->
            if (response.responseCode == OK && purchases != null) {
                for (purchase in purchases) {
                    consumeAsync(purchase.purchaseToken)
                }
            }
        }.enablePendingPurchases().build()

        val dialogBinding = LayoutDonateBinding.bind(dialog.view)

        client.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                dialog.view.post {
                    dialogBinding.paypalTitle.isVisible = result.responseCode != OK
                    dialogBinding.paypalButton.isVisible = result.responseCode != OK

                    dialogBinding.googlePlayDonateTitle.isVisible = result.responseCode == OK
                    dialogBinding.googlePlayDonate.isVisible = result.responseCode == OK
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
            client.launchBillingFlow(activity, BillingFlowParams.newBuilder().setSkuDetails(list[0]).build())
        }
    }

    fun onDonatePayPalClicked() {
        dialog.context.launchUrl("https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=zachary.wander@gmail.com")
    }

    private fun Context.extractActivity(): Activity {
        return when (this) {
            is Activity -> this
            is ContextThemeWrapper -> baseContext.extractActivity()
            is androidx.appcompat.view.ContextThemeWrapper -> baseContext.extractActivity()
            else -> throw IllegalArgumentException("Unable to extract Activity from ${javaClass.canonicalName}")
        }
    }
}