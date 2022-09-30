package com.zacharee1.systemuituner.activities

import android.content.Context
import android.content.Intent
import android.content.pm.*
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.reddit.indicatorfastscroll.FastScrollItemIndicator
import com.reddit.indicatorfastscroll.FastScrollerView
import com.zacharee1.systemuituner.ILockscreenShortcutSelectedCallback
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.databinding.AppActivityItemBinding
import com.zacharee1.systemuituner.databinding.LockscreenShortcutSelectorBinding
import com.zacharee1.systemuituner.util.*
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

    private val binding by lazy { LockscreenShortcutSelectorBinding.inflate(layoutInflater) }
    private val callback by lazy {
        val binder = intent.getBundleExtra(EXTRA_CALLBACK)?.getBinder(EXTRA_CALLBACK)
        if (binder != null) {
            ILockscreenShortcutSelectedCallback.Stub.asInterface(binder)
        } else null
    }
    private val key by lazy { intent.getStringExtra(EXTRA_KEY) }

    private val activityAdapter = ActivityAdapter { act ->
        callback?.callSafely {
            it.onSelected(act.component.flattenToShortString(), key)
        }
        finish()
    }

    private val appAdapter = AppAdapter {
        launch {
            when (it) {
                is LoadedApplicationInfo -> {
                    searchView?.setQuery("", false)
                    searchView?.isIconified = true

                    val deferred = async {
                        it.orig.activities?.map { LoadedActivityInfo(packageManager, it) }
                    }

                    activityAdapter.items.clear()
                    activityAdapter.items.addAll(deferred.await() ?: return@launch)

                    updateRecyclerVisibility(true)
                }

                else -> {
                    callback?.callSafely { callback ->
                        callback.onSelected(it.key, key)
                    }
                    finish()
                }
            }
        }
    }

    private var searchView: SearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (callback == null || key == null) {
            finish()
            return
        }

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.addAnimation()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.appSelector.adapter = appAdapter
        binding.activitySelector.adapter = activityAdapter

        binding.appSelectorScroller.setupWithRecyclerView(
            binding.appSelector,
            { position ->
                FastScrollItemIndicator.Text(
                    appAdapter.visibleItems[position].label.run {
                        if (isBlank()) "?" else substring(0, 1)
                    }.uppercase()
                )
            }
        )
        binding.activitySelectorScroller.setupWithRecyclerView(
            binding.activitySelector,
            { position ->
                FastScrollItemIndicator.Text(
                    activityAdapter.visibleItems[position].label.run {
                        if (isBlank()) "?" else substring(0, 1)
                    }.uppercase()
                )
            }
        )

        binding.appSelectorScroller.useDefaultScroller = false
        binding.appSelectorScroller.itemIndicatorSelectedCallbacks += object : FastScrollerView.ItemIndicatorSelectedCallback {
            override fun onItemIndicatorSelected(
                indicator: FastScrollItemIndicator,
                indicatorCenterY: Int,
                itemPosition: Int
            ) {
                binding.appSelector.scrollToPosition(itemPosition)
            }
        }

        binding.activitySelectorScroller.useDefaultScroller = false
        binding.activitySelectorScroller.itemIndicatorSelectedCallbacks += object : FastScrollerView.ItemIndicatorSelectedCallback {
            override fun onItemIndicatorSelected(
                indicator: FastScrollItemIndicator,
                indicatorCenterY: Int,
                itemPosition: Int
            ) {
                binding.activitySelector.scrollToPosition(itemPosition)
            }
        }

        val backPressedCallback = object : OnBackPressedCallback(binding.activitySelectorWrapper.isVisible) {
            override fun handleOnBackPressed() {
                updateRecyclerVisibility(false)
            }
        }
        binding.activitySelectorWrapper.visibilityChanged(this) {
            backPressedCallback.isEnabled = it.isVisible
        }

        onBackPressedDispatcher.addCallback(backPressedCallback)

        launch {
            val deferred = async {
                packageManager.getInstalledPackagesCompat(PackageManager.GET_ACTIVITIES)
                    .filter { it.activities != null && it.activities.isNotEmpty() }
                    .map { LoadedApplicationInfo(packageManager, it) }
            }

            appAdapter.items.apply {
                clear()
                if (isTouchWiz) {
                    addAll(
                        FlashlightSpecialInfo(this@LockscreenShortcutSelector),
                        DNDSpecialInfo(this@LockscreenShortcutSelector),
                        NoneSpecialInfo(this@LockscreenShortcutSelector)
                    )
                }
                addAll(deferred.await())
            }

            binding.progressWrapper.isVisible = false
            updateRecyclerVisibility(false)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)

        searchView = menu.findItem(R.id.search).actionView as SearchView?

        searchView?.setOnQueryTextListener(this)
        searchView?.addAnimation()
        searchView?.setOnSearchClickListener {
            searchView?.setQuery(if (!binding.appSelectorWrapper.isVisible) activityAdapter.query else appAdapter.query, false)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        when {
            binding.appSelectorWrapper.isVisible -> {
                appAdapter.query = newText
            }
            binding.activitySelectorWrapper.isVisible -> {
                activityAdapter.query = newText
            }
        }

        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    private fun updateRecyclerVisibility(forActivity: Boolean) {
        binding.activitySelectorWrapper.scaleAnimatedVisible = forActivity
        binding.appSelectorWrapper.scaleAnimatedVisible = !forActivity
    }

    class AppAdapter(private val selectionCallback: (ApplicationInfoItem) -> Unit) : BaseAdapter<ApplicationInfoItem>() {
        override val items = object : SortedList<ApplicationInfoItem>(
            ApplicationInfoItem::class.java,
            object : Callback<ApplicationInfoItem>() {
                override fun areItemsTheSame(
                    item1: ApplicationInfoItem?,
                    item2: ApplicationInfoItem?
                ): Boolean {
                    return item1 == item2
                }

                override fun onMoved(fromPosition: Int, toPosition: Int) {}
                override fun onChanged(position: Int, count: Int) {}
                override fun onInserted(position: Int, count: Int) {}
                override fun onRemoved(position: Int, count: Int) {}

                override fun compare(o1: ApplicationInfoItem, o2: ApplicationInfoItem): Int {
                    if (o1 is SpecialApplicationInfo && o2 !is SpecialApplicationInfo) {
                        return -1
                    }

                    if (o2 is SpecialApplicationInfo && o1 !is SpecialApplicationInfo) {
                        return 1
                    }

                    return o1.label.toString().compareTo(o2.label.toString())
                }

                override fun areContentsTheSame(
                    oldItem: ApplicationInfoItem,
                    newItem: ApplicationInfoItem
                ): Boolean {
                    return oldItem.key == newItem.key
                }
            }
        ) {
            override fun add(item: ApplicationInfoItem): Int {
                if (matchesQuery(item)) visibleItems.add(item)
                return super.add(item)
            }

            override fun addAll(vararg items: ApplicationInfoItem) {
                visibleItems.addAll(items.filter { matchesQuery(it) })
                super.addAll(*items)
            }

            override fun addAll(items: MutableCollection<ApplicationInfoItem>) {
                visibleItems.addAll(items.filter { matchesQuery(it) })
                super.addAll(items)
            }

            override fun addAll(items: Array<out ApplicationInfoItem>, mayModifyInput: Boolean) {
                visibleItems.addAll(items.filter { matchesQuery(it) })
                super.addAll(items, mayModifyInput)
            }

            override fun clear() {
                super.clear()
                visibleItems.clear()
            }
        }

        override val visibleItems = SortedList(
            ApplicationInfoItem::class.java,
            object : SortedList.Callback<ApplicationInfoItem>() {
                override fun areItemsTheSame(
                    item1: ApplicationInfoItem?,
                    item2: ApplicationInfoItem?
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

                override fun compare(o1: ApplicationInfoItem, o2: ApplicationInfoItem): Int {
                    if (o1 is SpecialApplicationInfo && o2 !is SpecialApplicationInfo) {
                        return -1
                    }

                    if (o2 is SpecialApplicationInfo && o1 !is SpecialApplicationInfo) {
                        return 1
                    }

                    return o1.label.toString().compareTo(o2.label.toString())
                }

                override fun areContentsTheSame(
                    oldItem: ApplicationInfoItem,
                    newItem: ApplicationInfoItem
                ): Boolean {
                    return oldItem.key == newItem.key
                }
            }
        )

        override fun onBindViewHolder(holder: VH, position: Int) {
            val itemBinding = AppActivityItemBinding.bind(holder.itemView)
            holder.itemView.apply {
                val info = visibleItems[position]

                itemBinding.icon.setImageDrawable(info.loadIcon(context.packageManager))
                itemBinding.name.text = info.label
                itemBinding.component.text = info.key

                setOnClickListener {
                    val newPosition = holder.bindingAdapterPosition
                    if (newPosition != -1) {
                        val newInfo = visibleItems[newPosition]

                        selectionCallback(newInfo)
                    }
                }
            }
        }
    }

    class ActivityAdapter(private val selectionCallback: (ActivityInfo) -> Unit) : BaseAdapter<LoadedActivityInfo>() {
        override val items = object : SortedList<LoadedActivityInfo>(
            LoadedActivityInfo::class.java,
            object : Callback<LoadedActivityInfo>() {
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
                    return oldItem.component == newItem.component
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

        override val visibleItems = SortedList(
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
                    return oldItem.component == newItem.component
                }
            }
        )

        override fun onBindViewHolder(holder: VH, position: Int) {
            val itemBinding = AppActivityItemBinding.bind(holder.itemView)
            holder.itemView.apply {
                val info = visibleItems[position]

                itemBinding.icon.setImageDrawable(info.loadIcon(context.packageManager))
                itemBinding.name.text = info.label
                itemBinding.component.text = info.component.flattenToShortString()

                setOnClickListener {
                    val newInfo = visibleItems[holder.bindingAdapterPosition]

                    selectionCallback(newInfo)
                }
            }
        }
    }

    abstract class BaseAdapter<InfoType : Any> : RecyclerView.Adapter<VH>() {
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

        fun matchesQuery(item: Any, query: String? = this.query): Boolean {
            return query.isNullOrBlank() ||
                    (item is LoadedActivityInfo
                            && (item.label.toString().contains(query, true)
                            || item.packageName.contains(query, true)
                            || item.component.flattenToShortString().contains(query, true)
                            ))
                    ||
                    (item is ApplicationInfoItem
                            && (item.label.toString().contains(query, true)
                            || item.key.contains(query, true)
                    ))
        }
    }

    class VH(view: View) : RecyclerView.ViewHolder(view)

    interface ApplicationInfoItem {
        val label: CharSequence
        val key: String

        fun loadIcon(pm: PackageManager): Drawable?
    }

    abstract class SpecialApplicationInfo(override val label: CharSequence, override val key: String) : ApplicationInfoItem
    class FlashlightSpecialInfo(private val context: Context) : SpecialApplicationInfo(
        context.resources.getString(R.string.flashlight),
        "NoUnlockNeeded/Flashlight"
    ) {
        override fun loadIcon(pm: PackageManager): Drawable? {
            return ContextCompat.getDrawable(context, R.drawable.baseline_flashlight_on_24)
        }
    }
    class DNDSpecialInfo(private val context: Context) : SpecialApplicationInfo(
        context.resources.getString(R.string.icon_blacklist_do_not_disturb),
        "NoUnlockNeeded/Dnd"
    ) {
        override fun loadIcon(pm: PackageManager): Drawable? {
            return ContextCompat.getDrawable(context, R.drawable.do_not_disturb)
        }
    }
    class NoneSpecialInfo(private val context: Context) : SpecialApplicationInfo(
        context.resources.getString(R.string.immersive_none),
        "NoUnlockNeeded/None"
    ) {
        override fun loadIcon(pm: PackageManager): Drawable? {
            return null
        }
    }

    class LoadedApplicationInfo(packageManager: PackageManager, val orig: PackageInfo) : ApplicationInfo(orig.applicationInfo), ApplicationInfoItem {
        override val label: CharSequence = loadLabel(packageManager)
        override val key: String = packageName
    }

    class LoadedActivityInfo(packageManager: PackageManager, orig: ActivityInfo) : ActivityInfo(orig) {
        val label: CharSequence = loadLabel(packageManager)
    }
}