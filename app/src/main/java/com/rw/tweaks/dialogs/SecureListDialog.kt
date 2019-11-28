package com.rw.tweaks.dialogs

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.preference.ListPreference
import androidx.preference.PreferenceDialogFragmentCompat
import com.rw.tweaks.R
import com.rw.tweaks.prefs.secure.SecureListPreference
import kotlinx.android.synthetic.main.base_dialog_layout.view.*
import kotlinx.android.synthetic.main.list_dialog.view.*

class SecureListDialog : PreferenceDialogFragmentCompat() {
    companion object {
        fun newInstance(key: String): SecureListDialog {
            return SecureListDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_KEY, key)
                }
            }
        }
    }

    private var clickedIndex = -1

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).also {
            it.window.setWindowAnimations(R.style.DialogTheme)
        }
    }

    override fun onCreateDialogView(context: Context?): View {
        return View.inflate(context, R.layout.list_dialog_layout, null)
    }

    override fun onPrepareDialogBuilder(builder: AlertDialog.Builder) {
        builder.setPositiveButton(null, null)
    }

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)

        val list = View.inflate(requireContext(), R.layout.list_dialog, view.wrapper).select_dialog_listview as ListView
        val adapter = CheckedItemAdapter(requireContext(),
            androidx.appcompat.R.layout.select_dialog_singlechoice_material,
            android.R.id.text1,
            (preference as SecureListPreference).entries)

        list.adapter = adapter
        list.setItemChecked((preference as SecureListPreference).findIndexOfValue((preference as SecureListPreference).value), true)

        list.setOnItemClickListener { _, _, position, _ ->
            clickedIndex = position

            onClick(dialog, DialogInterface.BUTTON_POSITIVE)
            dialog?.dismiss()
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult && clickedIndex >= 0) {
            val preference = preference as ListPreference
            val value = preference.entryValues[clickedIndex].toString()
            if (preference.callChangeListener(value)) {
                preference.value = value
            }
        }
    }

    private class CheckedItemAdapter(
        context: Context?, resource: Int, textViewResourceId: Int,
        objects: Array<CharSequence?>?
    ) :
        ArrayAdapter<CharSequence?>(context, resource, textViewResourceId, objects) {
        override fun hasStableIds(): Boolean {
            return true
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }
    }
}