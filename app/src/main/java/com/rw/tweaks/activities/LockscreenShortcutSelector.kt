package com.rw.tweaks.activities

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.content.pm.PackageItemInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.rw.tweaks.ILockscreenShortcutSelectedCallback
import com.rw.tweaks.R
import com.rw.tweaks.util.addAnimation
import kotlinx.android.synthetic.main.app_activity_item.view.*
import kotlinx.android.synthetic.main.lockscreen_shortcut_selector.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

class LockscreenShortcutSelector : AppCompatActivity(), CoroutineScope by MainScope(),
    SearchView.OnQueryTextListener {
    companion object {
        private const val EXTRA_CALLBACK = "callback"
        private const val EXTRA_KEY = "key"

        fun start(context: Context, key: String, callback: ILockscreenShortcutSelectedCallback) {
            context.startActivity(Intent(context, LockscreenShortcutSelector::class.java).apply {
                putExtra(EXTRA_CALLBACK, Bundle().apply {
                    putBinder(EXTRA_CALLBACK, callback.asBinder())
                })
                putExtra(EXTRA_KEY, key)
            })
        }
    }

    private val callback by lazy {
        ILockscreenShortcutSelectedCallback.Stub.asInterface(
            intent.getBundleExtra(EXTRA_CALLBACK).getBinder(EXTRA_CALLBACK)
        )
    }
    private val key by lazy { intent.getStringExtra(EXTRA_KEY) }

    private val activityAdapter = ActivityAdapter {
        callback.onSelected(it.componentName.flattenToShortString(), key)
        finish()
    }

    private val appAdapter = AppAdapter {
        launch {
            searchView?.setQuery("", false)
            searchView?.isIconified = true

            app_selector.visibility = View.GONE
            progress.visibility = View.VISIBLE

            val deferred = async {
                val pInfo =
                    packageManager.getPackageInfo(it.packageName, PackageManager.GET_ACTIVITIES)
                pInfo.activities.map { LoadedActivityInfo(packageManager, it) }
            }

            activityAdapter.items.apply {
                clear()
                addAll(deferred.await())
            }

            progress.visibility = View.GONE
            activity_selector.visibility = View.VISIBLE
        }
    }

    private var searchView: SearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.lockscreen_shortcut_selector)
        setSupportActionBar(toolbar)
        toolbar.addAnimation()

        app_selector.adapter = appAdapter
        activity_selector.adapter = activityAdapter

        launch {
            val deferred = async {
                packageManager.getInstalledApplications(0)
                    .map { LoadedApplicationInfo(packageManager, it) }
            }

            appAdapter.items.apply {
                clear()
                addAll(deferred.await())
            }

            progress.visibility = View.GONE
            app_selector.visibility = View.VISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)

        searchView = menu.findItem(R.id.search).actionView as SearchView?

        searchView?.setOnQueryTextListener(this)
        searchView?.addAnimation()

        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        when {
            app_selector.isVisible -> {
                activityAdapter.query = null
                appAdapter.query = newText
            }
            activity_selector.isVisible -> {
                appAdapter.query = null
                activityAdapter.query = newText
            }
        }

        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    class AppAdapter(private val selectionCallback: (ApplicationInfo) -> Unit) : BaseAdapter<LoadedApplicationInfo>() {
        override val items = object : SortedList<LoadedApplicationInfo>(
            LoadedApplicationInfo::class.java,
            object : SortedList.Callback<LoadedApplicationInfo>() {
                override fun areItemsTheSame(
                    item1: LoadedApplicationInfo?,
                    item2: LoadedApplicationInfo?
                ): Boolean {
                    return item1 == item2
                }

                override fun onMoved(fromPosition: Int, toPosition: Int) {}
                override fun onChanged(position: Int, count: Int) {}
                override fun onInserted(position: Int, count: Int) {}
                override fun onRemoved(position: Int, count: Int) {}

                override fun compare(o1: LoadedApplicationInfo, o2: LoadedApplicationInfo): Int {
                    return o1.label.toString().compareTo(o2.label.toString())
                }

                override fun areContentsTheSame(
                    oldItem: LoadedApplicationInfo,
                    newItem: LoadedApplicationInfo
                ): Boolean {
                    return oldItem.packageName == newItem.packageName
                }
            }
        ) {
            override fun add(item: LoadedApplicationInfo): Int {
                if (matchesQuery(item)) visibleItems.add(item)
                return super.add(item)
            }

            override fun addAll(vararg items: LoadedApplicationInfo) {
                visibleItems.addAll(items.filter { matchesQuery(it) })
                super.addAll(*items)
            }

            override fun addAll(items: MutableCollection<LoadedApplicationInfo>) {
                visibleItems.addAll(items.filter { matchesQuery(it) })
                super.addAll(items)
            }

            override fun addAll(items: Array<out LoadedApplicationInfo>, mayModifyInput: Boolean) {
                visibleItems.addAll(items.filter { matchesQuery(it) })
                super.addAll(items, mayModifyInput)
            }

            override fun clear() {
                super.clear()
                visibleItems.clear()
            }
        }

        override val visibleItems = SortedList<LoadedApplicationInfo>(
            LoadedApplicationInfo::class.java,
            object : SortedList.Callback<LoadedApplicationInfo>() {
                override fun areItemsTheSame(
                    item1: LoadedApplicationInfo?,
                    item2: LoadedApplicationInfo?
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

                override fun compare(o1: LoadedApplicationInfo, o2: LoadedApplicationInfo): Int {
                    return o1.label.toString().compareTo(o2.label.toString())
                }

                override fun areContentsTheSame(
                    oldItem: LoadedApplicationInfo,
                    newItem: LoadedApplicationInfo
                ): Boolean {
                    return oldItem.packageName == newItem.packageName
                }
            }
        )

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.itemView.apply {
                val info = visibleItems[position]

                icon.setImageDrawable(info.loadIcon(context.packageManager))
                name.text = info.label
                component.text = info.packageName

                setOnClickListener {
                    val newInfo = visibleItems[holder.adapterPosition]

                    selectionCallback(newInfo)
                }
            }
        }
    }

    class ActivityAdapter(private val selectionCallback: (ActivityInfo) -> Unit) : BaseAdapter<LoadedActivityInfo>() {
        override val items = object : SortedList<LoadedActivityInfo>(
            LoadedActivityInfo::class.java,
            object : SortedList.Callback<LoadedActivityInfo>() {
                override fun areItemsTheSame(
                    item1: LoadedActivityInfo?,
                    item2: LoadedActivityInfo?
                ): Boolean {
                    return item1 == item2
                }

                override fun onMoved(fromPosition: Int, toPosition: Int) {}
                override fun onChanged(position: Int, count: Int) {}
                override fun onInserted(position: Int, count: Int) {}
                override fun onRemoved(position: Int, count: Int) {}

                override fun compare(o1: LoadedActivityInfo, o2: LoadedActivityInfo): Int {
                    return o1.label.toString().compareTo(o2.label.toString())
                }

                override fun areContentsTheSame(
                    oldItem: LoadedActivityInfo,
                    newItem: LoadedActivityInfo
                ): Boolean {
                    return oldItem.componentName == newItem.componentName
                }
            }
        ) {
            override fun add(item: LoadedActivityInfo): Int {
                if (matchesQuery(item)) visibleItems.add(item)
                return super.add(item)
            }

            override fun addAll(vararg items: LoadedActivityInfo) {
                visibleItems.addAll(items.filter { matchesQuery(it) })
                super.addAll(*items)
            }

            override fun addAll(items: MutableCollection<LoadedActivityInfo>) {
                visibleItems.addAll(items.filter { matchesQuery(it) })
                super.addAll(items)
            }

            override fun addAll(items: Array<out LoadedActivityInfo>, mayModifyInput: Boolean) {
                visibleItems.addAll(items.filter { matchesQuery(it) })
                super.addAll(items, mayModifyInput)
            }

            override fun clear() {
                super.clear()
                visibleItems.clear()
            }
        }

        override val visibleItems = SortedList<LoadedActivityInfo>(
            LoadedActivityInfo::class.java,
            object : SortedList.Callback<LoadedActivityInfo>() {
                override fun areItemsTheSame(
                    item1: LoadedActivityInfo?,
                    item2: LoadedActivityInfo?
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

                override fun compare(o1: LoadedActivityInfo, o2: LoadedActivityInfo): Int {
                    return o1.label.toString().compareTo(o2.label.toString())
                }

                override fun areContentsTheSame(
                    oldItem: LoadedActivityInfo,
                    newItem: LoadedActivityInfo
                ): Boolean {
                    return oldItem.componentName == newItem.componentName
                }
            }
        )

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.itemView.apply {
                val info = visibleItems[position]

                icon.setImageDrawable(info.loadIcon(context.packageManager))
                name.text = info.label
                component.text = info.componentName.flattenToShortString()

                setOnClickListener {
                    val newInfo = visibleItems[holder.adapterPosition]

                    selectionCallback(newInfo)
                }
            }
        }
    }

    abstract class BaseAdapter<InfoType : PackageItemInfo> : RecyclerView.Adapter<VH>() {
        var query: String? = null
            set(value) {
                if (field != value) {
                    field = value

                    visibleItems.clear()

                    val toAdd = ArrayList<InfoType>()
                    for (i in 0 until items.size()) {
                        val item = items.get(i)

                        if (matchesQuery(item, value)) toAdd.add(item)
                    }

                    visibleItems.addAll(toAdd)
                }
            }

        abstract val items: SortedList<InfoType>
        abstract val visibleItems: SortedList<InfoType>

        override fun getItemCount(): Int {
            return visibleItems.size()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.app_activity_item,
                    parent,
                    false
                )
            )
        }

        fun matchesQuery(item: PackageItemInfo, query: String? = this.query): Boolean {
            val lowercase = query?.toLowerCase(Locale.getDefault())
            return lowercase.isNullOrBlank() ||
                    (item is LoadedActivityInfo
                            && (item.label.toString().toLowerCase(Locale.getDefault()).contains(lowercase)
                            || item.packageName.toLowerCase(Locale.getDefault()).contains(lowercase)
                            || item.componentName.flattenToShortString().toLowerCase(Locale.getDefault()).contains(lowercase)
                            ))
                    ||
                    (item is LoadedApplicationInfo
                            && (item.label.toString().toLowerCase(Locale.getDefault()).contains(lowercase)
                            || item.packageName.toLowerCase(Locale.getDefault()).contains(lowercase)
                    ))
        }
    }

    class VH(view: View) : RecyclerView.ViewHolder(view)

    class LoadedApplicationInfo(packageManager: PackageManager, orig: ApplicationInfo) :
        ApplicationInfo(orig) {
        val label = loadLabel(packageManager)
    }

    class LoadedActivityInfo(packageManager: PackageManager, orig: ActivityInfo) :
        ActivityInfo(orig) {
        val label = loadLabel(packageManager)
    }
}