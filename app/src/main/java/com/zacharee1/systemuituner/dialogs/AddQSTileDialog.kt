package com.zacharee1.systemuituner.dialogs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activities.QSEditorActivity
import com.zacharee1.systemuituner.data.QSTileInfo
import kotlinx.android.synthetic.main.add_qs_tile_item.view.*
import kotlinx.android.synthetic.main.dialog_add_intent_qs.view.*
import kotlinx.android.synthetic.main.dialog_add_qs_tile.view.*
import java.util.*

class AddQSTileDialog(context: Context, private val adapter: QSEditorActivity.QSEditorAdapter) : MaterialAlertDialogBuilder(context) {
        private val view: View = LayoutInflater.from(context).inflate(R.layout.dialog_add_qs_tile, null)
        private val intentString = context.resources.getString(R.string.intent)

        init {
            setTitle(R.string.add)
            setView(view)
            setNegativeButton(android.R.string.cancel, null)
        }

        override fun create(): AlertDialog {
            return super.create().also { dialog ->
                view.add_qs_tile_list.adapter = AddQSTileAdapter(adapter.availableTiles + intentString) {
                    dialog.dismiss()

                    if (it.equals(intentString, true)) {
                        val intentView = LayoutInflater.from(context).inflate(R.layout.dialog_add_intent_qs, null)
                        MaterialAlertDialogBuilder(context)
                            .setTitle(R.string.intent)
                            .setView(intentView)
                            .setPositiveButton(android.R.string.ok) { _, _ ->
                                val text = intentView.intent_text.text?.toString()

                                if (!text.isNullOrBlank()) {
                                    adapter.addTile(QSTileInfo("intent($text)"))
                                }
                            }
                            .show()
                    } else {
                        adapter.addTile(QSTileInfo(it))
                    }
                }
            }
        }

        class AddQSTileAdapter(private val items: List<String>, private val selectionCallback: (String) -> Unit) : RecyclerView.Adapter<AddQSTileAdapter.AddQSTileVH>() {
            override fun getItemCount(): Int {
                return items.size
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddQSTileVH {
                return AddQSTileVH(
                    LayoutInflater.from(parent.context).inflate(R.layout.add_qs_tile_item, parent, false)
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
                    itemView.qs_tile_name.text = key.capitalize(Locale.US)
                }
            }
        }
    }