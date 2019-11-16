package com.rw.tweaks.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
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
import java.util.*
import kotlin.collections.ArrayList

class ImmersiveListSelector : AppCompatActivity(), CoroutineScope by MainScope(), SearchView.OnQueryTextListener, SearchView.OnCloseListener {
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

    override fun onClose(): Boolean {
        adapter.currentQuery = null
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        adapter.currentQuery = newText
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)

        val searchItem = menu.findItem(R.id.search)
        val searchView = searchItem.actionView as SearchView?
        searchView?.setOnQueryTextListener(this)
        searchView?.setOnCloseListener(this)

        return true
    }

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
        var currentQuery: String? = null
            set(value) {
                field = value

                filteredItems.clear()

                val toAdd = ArrayList<LoadedAppInfo>()
                for (i in 0 until items.size()) {
                    val item = items.get(i)

                    if (matches(value, item)) toAdd.add(item)
                }

                filteredItems.addAll(toAdd)
            }

        private val filteredItems = SortedList<LoadedAppInfo>(
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
            }
        )

        private val items = object : SortedList<LoadedAppInfo>(
            LoadedAppInfo::class.java,
            object : SortedList.Callback<LoadedAppInfo>() {
                override fun areItemsTheSame(
                    item1: LoadedAppInfo,
                    item2: LoadedAppInfo
                ): Boolean {
                    return item1 == item2
                }

                override fun onMoved(fromPosition: Int, toPosition: Int) {}
                override fun onChanged(position: Int, count: Int) {}
                override fun onInserted(position: Int, count: Int) {}
                override fun onRemoved(position: Int, count: Int) {}

                override fun compare(o1: LoadedAppInfo, o2: LoadedAppInfo): Int {
                    return o1.label.compareTo(o2.label)
                }

                override fun areContentsTheSame(
                    oldItem: LoadedAppInfo,
                    newItem: LoadedAppInfo
                ): Boolean {
                    return oldItem.packageName == newItem.packageName
                }
            }) {

            override fun add(item: LoadedAppInfo): Int {
                if (matches(currentQuery, item)) filteredItems.add(item)
                return super.add(item)
            }

            override fun addAll(vararg items: LoadedAppInfo) {
                filteredItems.addAll(items.filter { matches(currentQuery, it) })
                super.addAll(*items)
            }

            override fun addAll(items: Collection<LoadedAppInfo>) {
                filteredItems.addAll(items.filter { matches(currentQuery, it) })
                super.addAll(items)
            }

            override fun addAll(items: Array<out LoadedAppInfo>, mayModifyInput: Boolean) {
                filteredItems.addAll(items.filter { matches(currentQuery, it) })
                super.addAll(items, mayModifyInput)
            }

            override fun remove(item: LoadedAppInfo): Boolean {
                filteredItems.remove(item)
                return super.remove(item)
            }

            override fun removeItemAt(index: Int): LoadedAppInfo {
                val removed = super.removeItemAt(index)
                filteredItems.indexOf(removed).apply {
                    if (this != -1) filteredItems.removeItemAt(this)
                }
                return removed
            }
        }

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
            return filteredItems.size()
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val info = filteredItems.get(position)
            holder.itemView.apply {
                name.text = info.label
                package_name.text = info.packageName
                icon.setImageDrawable(info.icon)
                check.isChecked = info.isChecked

                setOnClickListener {
                    val currentInfo = filteredItems.get(holder.adapterPosition)
                    currentInfo.isChecked = !currentInfo.isChecked
                    check.isChecked = currentInfo.isChecked
                }
            }
        }

        fun setItems(items: List<LoadedAppInfo>) {
            this.items.clear()
            this.items.addAll(items)
        }

        fun matches(query: String?, item: LoadedAppInfo): Boolean {
            val lowercaseQuery = query?.toLowerCase(Locale.getDefault())
            val lowercaseAppName = item.label.toLowerCase(Locale.getDefault())
            val lowercasePackageName = item.packageName.toLowerCase(Locale.getDefault())

            return lowercaseQuery.isNullOrBlank()
                    || lowercaseAppName.contains(lowercaseQuery)
                    || lowercasePackageName.contains(lowercaseQuery)
        }
    }

    class VH(view: View) : RecyclerView.ViewHolder(view)
}