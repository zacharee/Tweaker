package com.rw.tweaks.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import androidx.recyclerview.widget.RecyclerView
import com.rw.tweaks.R
import com.rw.tweaks.prefs.secure.SecureListPreference
import kotlinx.android.synthetic.main.base_dialog_layout.view.*
import kotlinx.android.synthetic.main.list_dialog.view.*

class SecureListDialog : BaseOptionDialog() {
    companion object {
        fun newInstance(key: String): SecureListDialog {
            return SecureListDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_KEY, key)
                }
            }
        }
    }

    private val listPref: SecureListPreference
        get() = preference as SecureListPreference

    private var clickedIndex = -1

    override val layoutRes: Int = 0

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)

        val checkedIndex = listPref.findIndexOfValue(listPref.value)
        val entries = (preference as SecureListPreference).entries
        val list = View.inflate(requireContext(), R.layout.list_dialog, view.wrapper).select_dialog_listview as RecyclerView
        val adapter = Adapter(entries?.mapIndexed { index, charSequence -> ItemInfo(charSequence, index == checkedIndex) } ?: ArrayList()) {
            clickedIndex = it
            onClick(dialog, DialogInterface.BUTTON_POSITIVE)
            dialog?.dismiss()
        }

        list.adapter = adapter
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult && clickedIndex >= 0) {
            val preference = preference as SecureListPreference
            val value = preference.entryValues!![clickedIndex].toString()
            if (preference.callChangeListener(value)) {
                preference.value = value
                preference.onValueChanged(value, preference.writeKey)
            }
        }
    }

    private data class ItemInfo(
        val label: CharSequence?,
        var isChecked: Boolean
    )

    private class Adapter(private val items: List<ItemInfo>, private val clickCallback: (index: Int) -> Unit) : RecyclerView.Adapter<Adapter.VH>() {
        init {
            setHasStableIds(true)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(LayoutInflater.from(parent.context).inflate(androidx.appcompat.R.layout.select_dialog_singlechoice_material, parent, false))
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.itemView.apply {
                findViewById<CheckedTextView>(android.R.id.text1).apply {
                    text = items[position].label
                    isChecked = items[position].isChecked
                }

                setOnClickListener {
                    val pos = holder.adapterPosition

                    setChecked(pos, true)
                }
            }
        }

        fun setChecked(index: Int, checked: Boolean) {
            items.filter { it.isChecked }.forEach { itemInfo ->
                itemInfo.isChecked = false
            }
            items[index].isChecked = checked

            notifyDataSetChanged()
        }

        class VH(view: View) : RecyclerView.ViewHolder(view)
    }
}