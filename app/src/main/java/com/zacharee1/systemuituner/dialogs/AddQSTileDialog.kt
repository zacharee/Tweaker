package com.zacharee1.systemuituner.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activities.QSEditorActivity
import com.zacharee1.systemuituner.data.QSTileInfo
import com.zacharee1.systemuituner.databinding.DialogAddCustomQsBinding
import com.zacharee1.systemuituner.databinding.DialogAddIntentQsBinding
import com.zacharee1.systemuituner.databinding.DialogAddQsTileBinding
import com.zacharee1.systemuituner.databinding.QsTileBinding
import com.zacharee1.systemuituner.util.dpAsPx

class AddQSTileDialog(context: Context, private val adapter: QSEditorActivity.QSEditorAdapter) :
    RoundedBottomSheetDialog(context) {
    @SuppressLint("InflateParams")
    private val view: View = LayoutInflater.from(context).inflate(R.layout.dialog_add_qs_tile, null)
    private val qsBinding = DialogAddQsTileBinding.bind(view)
    private val intentString = context.resources.getString(R.string.intent)
    private val customString = context.resources.getString(R.string.add_custom_item)

    init {
        setTitle(R.string.add_qs_tile)
        setNegativeButton(android.R.string.cancel, null)
    }

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setLayout(view)

        baseBinding.positiveButton.isVisible = false

        qsBinding.addQsTileList.adapter = AddQSTileAdapter(adapter.availableTiles + intentString + customString) {
            dismiss()

            when (it) {
                intentString -> {
                    val intentView = LayoutInflater.from(context).inflate(R.layout.dialog_add_intent_qs, null)
                    val intentBinding = DialogAddIntentQsBinding.bind(intentView)
                    val dialog = RoundedBottomSheetDialog(context).apply {
                        setTitle(R.string.intent)
                        setLayout(intentView)
                    }
                    dialog.setPositiveButton(android.R.string.ok) { _, _ ->
                        val text = intentBinding.intentText.text?.toString()

                        if (!text.isNullOrBlank()) {
                            adapter.addTile(QSTileInfo("intent($text)"))
                        }

                        dialog.dismiss()
                    }
                    dialog.show()
                }
                customString -> {
                    val intentView = LayoutInflater.from(context).inflate(R.layout.dialog_add_custom_qs, null)
                    val intentBinding = DialogAddCustomQsBinding.bind(intentView)
                    val dialog = RoundedBottomSheetDialog(context).apply {
                        setTitle(R.string.tile_custom)
                        setLayout(intentView)
                    }
                    dialog.setPositiveButton(android.R.string.ok) { _, _ ->
                        val text = intentBinding.customText.text?.toString()

                        if (!text.isNullOrBlank()) {
                            adapter.addTile(QSTileInfo(text))
                        }

                        dialog.dismiss()
                    }
                    dialog.show()
                }
                else -> {
                    adapter.addTile(QSTileInfo(it))
                }
            }
        }
    }

    class AddQSTileAdapter(
        private val items: List<String>,
        private val selectionCallback: (String) -> Unit
    ) : RecyclerView.Adapter<AddQSTileAdapter.AddQSTileVH>() {
        override fun getItemCount(): Int {
            return items.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddQSTileVH {
            return AddQSTileVH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.qs_tile, parent, false)
            )
        }

        override fun onBindViewHolder(holder: AddQSTileVH, position: Int) {
            holder.onBind(items[position])
        }

        inner class AddQSTileVH(view: View) : RecyclerView.ViewHolder(view) {
            private val binding = QsTileBinding.bind(itemView)

            init {
                binding.clickTarget.setOnClickListener {
                    val newPos = bindingAdapterPosition

                    if (newPos != -1) {
                        selectionCallback(items[newPos])
                    }
                }

                binding.qsTileCard.setBackgroundColor(Color.TRANSPARENT)
            }

            fun onBind(key: String) {
                val info = QSTileInfo(key)

                itemView.layoutParams = (itemView.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    val dp8= itemView.context.dpAsPx(8)
                    val dp4 = itemView.context.dpAsPx(4)
                    setMargins(dp8, dp4, dp8, dp4)
                }
                (itemView as? MaterialCardView)?.apply {
                    cardElevation = 0f
                }
                binding.label.text = info.getLabel(itemView.context)
                binding.qsTileIcon.setImageDrawable(info.getIcon(itemView.context))
                binding.qsTileType.apply {
                    maxLines = Int.MAX_VALUE
                    if (info.type == QSTileInfo.Type.CUSTOM) {
                        isVisible = true
                        text = info.getNameAndComponentForCustom()?.flattenToShortString() ?: info.key
                    } else {
                        isVisible = false
                    }
                }
            }
        }
    }
}