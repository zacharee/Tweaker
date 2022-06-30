package com.zacharee1.systemuituner.prefs.nav

import android.content.Context
import android.util.AttributeSet
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.dialogs.RoundedBottomSheetDialog

class ShowDialogPreference(context: Context, attrs: AttributeSet?) : Preference(context, attrs) {
    private val dialogClass: Class<RoundedBottomSheetDialog>?

    init {
        dialogClass = if (attrs != null) {
            val array = context.theme.obtainStyledAttributes(attrs, R.styleable.ShowDialogPreference, 0, 0)

            array.getString(R.styleable.ShowDialogPreference_dialog_class)?.let {
                @Suppress("UNCHECKED_CAST")
                context.classLoader.loadClass(it) as Class<RoundedBottomSheetDialog>
            }
        } else {
            null
        }
    }

    override fun onClick() {
        super.onClick()

        dialogClass?.getConstructor(Context::class.java)?.newInstance(context)?.show()
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        holder.isDividerAllowedAbove = true
        holder.isDividerAllowedBelow = true
    }
}