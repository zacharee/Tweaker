package com.rw.tweaks.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.rw.tweaks.R
import com.rw.tweaks.data.LoadedAppInfo
import kotlinx.android.synthetic.main.activity_immersive_selector.*
import kotlinx.android.synthetic.main.immersive_app_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ImmersiveListSelector : AppCompatActivity(), CoroutineScope by MainScope() {
    companion object {
        const val EXTRA_CHECKED = "checked_packages"

        fun start(context: Context, checked: ArrayList<String>?) {
            val activity = Intent(context, ImmersiveListSelector::class.java)
            activity.putExtra(EXTRA_CHECKED, checked)

            context.startActivity(activity)
        }
    }

    private val checked by lazy {
        intent.getStringArrayListExtra(EXTRA_CHECKED) ?: ArrayList<String>()
    }
    private val adapter by lazy { Adapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_immersive_selector)

        setSupportActionBar(toolbar)

        list.adapter = adapter

        launch {
            val apps = async {
                packageManager.getInstalledApplications(0)
                    .map {
                        LoadedAppInfo(
                            label = it.loadLabel(packageManager).toString(),
                            packageName = it.packageName,
                            icon = it.loadIcon(packageManager),
                            isChecked = checked.contains(it.packageName)
                        )
                    }
            }

            adapter.setItems(apps.await())

            progress.visibility = View.GONE
            list.visibility = View.VISIBLE
        }
    }

    class Adapter : RecyclerView.Adapter<VH>() {
        private val items = SortedList<LoadedAppInfo>(
            LoadedAppInfo::class.java,
            object : SortedList.Callback<LoadedAppInfo>() {
                override fun areItemsTheSame(
                    item1: LoadedAppInfo,
                    item2: LoadedAppInfo
                ): Boolean {
                    return item1 == item2
                }

                override fun onMoved(fromPosition: Int, toPosition: Int) {
                    notifyItemMoved(fromPosition, toPosition)
                }

                override fun onChanged(position: Int, count: Int) {
                    notifyItemRangeChanged(position, count)
                }

                override fun onInserted(position: Int, count: Int) {
                    notifyItemRangeInserted(position, count)
                }

                override fun onRemoved(position: Int, count: Int) {
                    notifyItemRangeRemoved(position, count)
                }

                override fun compare(o1: LoadedAppInfo, o2: LoadedAppInfo): Int {
                    return o1.label.compareTo(o2.label)
                }

                override fun areContentsTheSame(
                    oldItem: LoadedAppInfo,
                    newItem: LoadedAppInfo
                ): Boolean {
                    return oldItem.packageName == newItem.packageName
                }

            })

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.immersive_app_item,
                    parent,
                    false
                )
            )
        }

        override fun getItemCount(): Int {
            return items.size()
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val info = items.get(position)
            holder.itemView.apply {
                name.text = info.label
                package_name.text = info.packageName
                icon.setImageDrawable(info.icon)
                check.isChecked = info.isChecked

                setOnClickListener {
                    val currentInfo = items.get(holder.adapterPosition)
                    currentInfo.isChecked = !currentInfo.isChecked
                    check.isChecked = currentInfo.isChecked
                }
            }
        }

        fun setItems(items: List<LoadedAppInfo>) {
            this.items.clear()
            this.items.addAll(items)
        }
    }

    class VH(view: View) : RecyclerView.ViewHolder(view)
}