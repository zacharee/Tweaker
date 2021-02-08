package com.zacharee1.systemuituner.dialogs

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.databinding.LayoutDonateBinding
import com.zacharee1.systemuituner.util.BillingUtil

class DonateDialog(val activity: Activity) : ScrolledRoundedBottomSheetDialog(activity) {
    val view: View = LayoutInflater.from(context).inflate(R.layout.layout_donate, null)

    private val binding = LayoutDonateBinding.bind(view)
    private val billingUtil = BillingUtil(this)

    init {
        setTitle(R.string.donate)
        setLayout(view)

        setPositiveButton(android.R.string.ok, null)

        view.apply {
            binding.paypalButton.setOnClickListener {
                BillingUtil.onDonatePayPalClicked(context)
            }
            binding.donate1.setOnClickListener {
                billingUtil.doDonate("donate_1")
            }
            binding.donate2.setOnClickListener {
                billingUtil.doDonate("donate_2")
            }
            binding.donate5.setOnClickListener {
                billingUtil.doDonate("donate_5")
            }
            binding.donate10.setOnClickListener {
                billingUtil.doDonate("donate_10")
            }
        }
    }
}