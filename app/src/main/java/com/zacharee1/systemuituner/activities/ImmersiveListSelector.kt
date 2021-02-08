package com.zacharee1.systemuituner.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.rey.material.widget.CheckedImageView
import com.zacharee1.systemuituner.IImmersiveSelectionCallback
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.data.LoadedAppInfo
import com.zacharee1.systemuituner.databinding.ActivityImmersiveSelectorBinding
import com.zacharee1.systemuituner.interfaces.ColorPreference
import com.zacharee1.systemuituner.util.addAnimation
import com.zacharee1.systemuituner.util.callSafely
import com.zacharee1.systemuituner.util.getColorPrimary
import kotlinx.coroutines.*

class ImmersiveListSelector : AppCompatActivity(), CoroutineScope by MainScope(), SearchView.OnQueryTextListener, SearchView.OnCloseListener {
    companion object {
        const val EXTRA_CHECKED = "checked_packages"
        const val EXTRA_CALLBACK = "callback"

        fun start(context: Context, checked: ArrayList<String>?, onResultListener: IImmersiveSelectionCallback) {
            val activity = Intent(context, ImmersiveListSelector::class.java)
            activity.putExtra(EXTRA_CHECKED, checked)
            activity.putExtra(EXTRA_CALLBACK, Bundle().apply { putBinder(EXTRA_CALLBACK, onResultListener.asBinder()) })

            context.startActivity(activity)
        }
    }

    val checked by lazy {
        HashSet(intent.getStringArrayListExtra(EXTRA_CHECKED) ?: ArrayList<String>())
    }
    val callback by lazy {
        val binder = intent.getBundleExtra(EXTRA_CALLBACK)?.getBinder(EXTRA_CALLBACK)
        if (binder != null) {
            IImmersiveSelectionCallback.Stub.asInterface(binder)
        } else null
    }
    private val adapter by lazy { ImmersiveAdapter(checked, this) }
    private val origItems = ArrayList<LoadedAppInfo>()
    private val binding by lazy { ActivityImmersiveSelectorBinding.inflate(layoutInflater) }

    override fun onClose(): Boolean {
        onFilter(null)
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        onFilter(newText)
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
        searchView?.addAnimation()

        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (callback == null) {
            finish()
            return
        }

        setSupportActionBar(binding.toolbar)
        binding.toolbar.addAnimation()

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_check_24)

        binding.selector.adapter = adapter

        launch {
            val apps = withContext(Dispatchers.IO) {
                packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES)
                    .filter { !it.activities.isNullOrEmpty() }
                    .map { it.applicationInfo }
                    .map {
                        LoadedAppInfo(
                            label = it.loadLabel(packageManager).toString(),
                            packageName = it.packageName,
                            icon = it.loadIcon(packageManager),
                            isChecked = checked.contains(it.packageName),
                            colorPrimary = it.getColorPrimary(this@ImmersiveListSelector)
                        )
                    }
            }

            setItems(apps)
            binding.progress.visibility = View.GONE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()

        cancel()
        callback?.callSafely {
            it.onImmersiveResult(ArrayList(checked))
        }
    }

    fun setItems(items: Collection<LoadedAppInfo>) {
        origItems.clear()
        origItems.addAll(items)

        adapter.items.replaceAll(origItems)
    }

    fun onFilter(query: String?) {
        adapter.items.replaceAll(origItems.filter { it.matchesQuery(query) })
    }

    @SuppressLint("RestrictedApi")
    class ImmersiveAdapter(private val checked: HashSet<String>, mainScope: CoroutineScope) : RecyclerView.Adapter<ImmersiveAdapter.ImmersiveVH>(), CoroutineScope by mainScope {
        enum class Payload {
            CHECKED_CHANGE
        }

        val items = ItemList()

        override fun getItemCount(): Int {
            return items.size()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImmersiveVH {
            return ImmersiveVH(
                LayoutInflater.from(parent.context).inflate(R.layout.custom_preference, parent, false).apply {
                    findViewById<LinearLayout>(android.R.id.widget_frame).apply {
                        LayoutInflater.from(context).inflate(R.layout.checkbox, this, true)
                    }
                }
            )
        }

        override fun onBindViewHolder(holder: ImmersiveVH, position: Int) {}

        override fun onBindViewHolder(
            holder: ImmersiveVH,
            position: Int,
            payloads: MutableList<Any>
        ) {
            super.onBindViewHolder(holder, position, payloads)
            holder.onBind(items[position], payloads.firstOrNull())
        }

        inner class ItemList : SortedList<LoadedAppInfo>(LoadedAppInfo::class.java,
            object : Callback<LoadedAppInfo>() {
                override fun areItemsTheSame(
                    item1: LoadedAppInfo?,
                    item2: LoadedAppInfo?
                ): Boolean {
                    return item1?.packageName == item2?.packageName
                }

                override fun areContentsTheSame(
                    oldItem: LoadedAppInfo?,
                    newItem: LoadedAppInfo?
                ): Boolean {
                    return oldItem?.packageName == newItem?.packageName
                }

                override fun compare(o1: LoadedAppInfo, o2: LoadedAppInfo): Int {
                    return if (o1.isChecked && !o2.isChecked) -1
                    else if (!o1.isChecked && o2.isChecked) 1
                    else o1.label.compareTo(o2.label)
                }

                override fun onInserted(position: Int, count: Int) {
                    notifyItemRangeInserted(position, count)
                }

                override fun onChanged(position: Int, count: Int) {
                    notifyItemRangeChanged(position, count)
                }

                override fun onRemoved(position: Int, count: Int) {
                    notifyItemRangeRemoved(position, count)
                }

                override fun onMoved(fromPosition: Int, toPosition: Int) {
                    notifyItemMoved(fromPosition, toPosition)
                }
            }
        )

        inner class ImmersiveVH(view: View) : RecyclerView.ViewHolder(view) {
            val title: TextView
                get() = itemView.findViewById(android.R.id.title)
            val summary: TextView
                get() = itemView.findViewById(android.R.id.summary)
            val icon: ImageView
                get() = itemView.findViewById(android.R.id.icon)
            val checkbox: CheckedImageView
                get() = itemView.findViewById(android.R.id.checkbox)

            private val colorPreference = ColorPreference(itemView.context, null)

            init {
                itemView.setOnClickListener {
                    val newPos = bindingAdapterPosition
                    if (newPos != -1) {
                        val newInfo = items[newPos]
                        val isChecked = checked.contains(newInfo.packageName)
                        if (!isChecked) {
                            checked.add(newInfo.packageName)
                        } else {
                            checked.remove(newInfo.packageName)
                        }

                        newInfo.isChecked = !isChecked
                        items.updateItemAt(newPos, newInfo)
                    }
                }
            }

            fun onBind(info: LoadedAppInfo, payload: Any?) {
                checkbox.isChecked = checked.contains(info.packageName)

                if (payload == Payload.CHECKED_CHANGE) return

                colorPreference.iconColor = info.colorPrimary
                colorPreference.bindVH(this)

                title.text = info.label
                summary.text = info.packageName
                icon.setImageDrawable(info.icon)
            }
        }
    }
}