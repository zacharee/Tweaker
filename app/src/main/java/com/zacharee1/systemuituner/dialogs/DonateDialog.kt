package com.zacharee1.systemuituner.dialogs

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.android.billingclient.api.SkuDetails
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.BillingUtil
import kotlinx.android.synthetic.main.layout_donate.view.*

class DonateDialog(val activity: Activity) : RoundedBottomSheetDialog(activity) {
    val view: View = LayoutInflater.from(context).inflate(R.layout.layout_donate, null)
    private val billingUtil = BillingUtil(this)

    init {
        setTitle(R.string.donate)
        setLayout(view)

        setPositiveButton(android.R.string.ok, null)

        view.apply {
            paypal_button.setOnClickListener {
                BillingUtil.onDonatePayPalClicked(context)
            }
            donate_1.setOnClickListener {
                billingUtil.doDonate("donate_1")
            }
            donate_2.setOnClickListener {
                billingUtil.doDonate("donate_2")
            }
            donate_5.setOnClickListener {
                billingUtil.doDonate("donate_5")
            }
            donate_10.setOnClickListener {
                billingUtil.doDonate("donate_10")
            }
        }
    }
}