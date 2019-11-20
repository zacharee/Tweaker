package com.rw.tweaks.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.rw.tweaks.IImmersiveSelectionCallback
import com.rw.tweaks.R
import com.rw.tweaks.activities.ImmersiveListSelector
import com.rw.tweaks.util.ImmersiveManager
import kotlinx.android.synthetic.main.immersive_mode.view.*
import kotlinx.android.synthetic.main.immersive_mode_item.view.*

class ImmersiveMode(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private val immersiveManager = ImmersiveManager(context)
    private val immersiveInfo = immersiveManager.parseAdvancedImmersive()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        mode_list.adapter = ImmersiveAdapter(immersiveInfo, immersiveManager)

        reset.setOnClickListener {
            immersiveInfo.clear()
            immersiveManager.setAdvancedImmersive(immersiveInfo)
            mode_list.adapter!!.notifyItemRangeChanged(0, mode_list.adapter!!.itemCount)
        }
    }

    class ImmersiveAdapter(
        private val immInfo: ImmersiveManager.ImmersiveInfo,
        private val manager: ImmersiveManager
    ) : RecyclerView.Adapter<ImmersiveAdapter.VH>() {
        private val items = arrayListOf(
            ItemInfo(
                R.string.immersive_full,
                ImmersiveManager.ImmersiveMode.FULL
            ),
            ItemInfo(
                R.string.immersive_status,
                ImmersiveManager.ImmersiveMode.STATUS
            ),
            ItemInfo(
                R.string.immersive_nav,
                ImmersiveManager.ImmersiveMode.NAV
            )
        )

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.immersive_mode_item,
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val info = items[position]

            holder.itemView.apply {
                immersive_name.text = resources.getText(info.name)
                all.isChecked = when (info.type) {
                    ImmersiveManager.ImmersiveMode.FULL -> immInfo.allFull
                    ImmersiveManager.ImmersiveMode.STATUS -> immInfo.allStatus
                    ImmersiveManager.ImmersiveMode.NAV -> immInfo.allNav
                    else -> false
                }
                whitelist_button.isEnabled = !all.isChecked

                all.setOnClickListener {
                    val newInfo = items[holder.adapterPosition]
                    all.isChecked = !all.isChecked

                    when (newInfo.type) {
                        ImmersiveManager.ImmersiveMode.FULL -> immInfo.allFull =
                            all.isChecked.also { if (it) immInfo.fullApps.clear() }
                        ImmersiveManager.ImmersiveMode.STATUS -> immInfo.allStatus =
                            all.isChecked.also { if (it) immInfo.statusApps.clear() }
                        ImmersiveManager.ImmersiveMode.NAV -> immInfo.allNav =
                            all.isChecked.also { if (it) immInfo.navApps.clear() }
                        else -> {
                        }
                    }

                    whitelist_button.isEnabled = !all.isChecked

                    update()
                }

                whitelist_button.setOnClickListener {
                    val newInfo = items[holder.adapterPosition]
                    val apps = when (newInfo.type) {
                        ImmersiveManager.ImmersiveMode.FULL -> immInfo.fullApps
                        ImmersiveManager.ImmersiveMode.STATUS -> immInfo.statusApps
                        ImmersiveManager.ImmersiveMode.NAV -> immInfo.navApps
                        else -> ArrayList()
                    }

                    ImmersiveListSelector.start(context, apps, ImmersiveSelectionCallbackWrapper {
                        apps.clear()
                        apps.addAll(it)
                        update()
                    })
                }

                blacklist.setOnClickListener {
                    val newInfo = items[holder.adapterPosition]
                    val apps = when (newInfo.type) {
                        ImmersiveManager.ImmersiveMode.FULL -> immInfo.fullBl
                        ImmersiveManager.ImmersiveMode.STATUS -> immInfo.statusBl
                        ImmersiveManager.ImmersiveMode.NAV -> immInfo.navBl
                        else -> ArrayList()
                    }

                    ImmersiveListSelector.start(context, apps, ImmersiveSelectionCallbackWrapper {
                        apps.clear()
                        apps.addAll(it)
                        update()
                    })
                }
            }
        }

        fun update() {
            manager.setAdvancedImmersive(immInfo)
        }

        class VH(view: View) : RecyclerView.ViewHolder(view)
    }

    data class ItemInfo(
        val name: Int,
        val type: ImmersiveManager.ImmersiveMode
    )

    private class ImmersiveSelectionCallbackWrapper(private val callback: (checked: List<String>) -> Unit) :
        IImmersiveSelectionCallback.Stub() {
        override fun onImmersiveResult(checked: MutableList<Any?>) {
            callback(checked.map { it.toString() })
        }
    }
}