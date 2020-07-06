package com.zacharee1.systemuituner.dialogs

import android.content.Context
import android.content.DialogInterface
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
import com.zacharee1.systemuituner.util.dpAsPx
import kotlinx.android.synthetic.main.base_dialog_layout.*
import kotlinx.android.synthetic.main.dialog_add_intent_qs.view.*
import kotlinx.android.synthetic.main.dialog_add_qs_tile.view.*
import kotlinx.android.synthetic.main.qs_tile.view.*

class AddQSTileDialog(context: Context, private val adapter: QSEditorActivity.QSEditorAdapter) :
    RoundedBottomSheetDialog(context) {
    private val view: View = LayoutInflater.from(context).inflate(R.layout.dialog_add_qs_tile, null)
    private val intentString = context.resources.getString(R.string.intent)

    init {
        setTitle(R.string.add)
        setNegativeButton(android.R.string.cancel, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setLayout(view)

        positive_button.isVisible = false

        view.add_qs_tile_list.adapter = AddQSTileAdapter(adapter.availableTiles + intentString) {
            dismiss()

            if (it.equals(intentString, true)) {
                val intentView = LayoutInflater.from(context).inflate(R.layout.dialog_add_intent_qs, null)
                val dialog = RoundedBottomSheetDialog(context).apply {
                    setTitle(R.string.intent)
                    setLayout(intentView)
                }
                dialog.setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { _, _ ->
                    val text = intentView.intent_text.text?.toString()

                    if (!text.isNullOrBlank()) {
                        adapter.addTile(QSTileInfo("intent($text)"))
                    }

                    dialog.dismiss()
                })
                dialog.show()
            } else {
                adapter.addTile(QSTileInfo(it))
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

        @ExperimentalStdlibApi
        override fun onBindViewHolder(holder: AddQSTileVH, position: Int) {
            holder.onBind(items[position])
        }

        inner class AddQSTileVH(view: View) : RecyclerView.ViewHolder(view) {
            init {
                itemView.setOnClickListener {
                    val newPos = bindingAdapterPosition

                    if (newPos != -1) {
                        selectionCallback(items[newPos])
                    }
                }
            }

            @ExperimentalStdlibApi
            fun onBind(key: String) {
                val info = QSTileInfo(key)

                itemView.layoutParams = (itemView.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    val dp8= itemView.context.dpAsPx(8)
                    val dp4 = itemView.context.dpAsPx(4)
                    setMargins(dp8, dp4, dp8, dp4)
                }
                (itemView as MaterialCardView).apply {
                    cardElevation = 0f
                }
                itemView.label.text = info.getLabel(itemView.context)
                itemView.qs_tile_icon.setImageDrawable(info.getIcon(itemView.context))
                itemView.qs_tile_component.apply {
                    if (info.type == QSTileInfo.Type.CUSTOM) {
                        isVisible = true
                        text = info.getNameAndComponentForCustom().flattenToShortString()
                    } else {
                        isVisible = false
                    }
                }
            }
        }
    }
}