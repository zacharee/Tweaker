package com.zacharee1.systemuituner.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.zacharee1.systemuituner.IImmersiveSelectionCallback
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.data.LoadedAppInfo
import com.zacharee1.systemuituner.fragments.ImmersiveSelectorFragment
import com.zacharee1.systemuituner.util.addAnimation
import com.zacharee1.systemuituner.util.getColorPrimary
import kotlinx.android.synthetic.main.activity_immersive_selector.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

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
        IImmersiveSelectionCallback.Stub.asInterface(intent.getBundleExtra(EXTRA_CALLBACK).getBinder(EXTRA_CALLBACK))
    }

    val selectorFragment: ImmersiveSelectorFragment
        get() = selector as ImmersiveSelectorFragment

    override fun onClose(): Boolean {
        selectorFragment.onFilter(null)
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        selectorFragment.onFilter(newText)
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
        setContentView(R.layout.activity_immersive_selector)

        setSupportActionBar(toolbar)
        toolbar.addAnimation()

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_check_24)

        launch {
            val apps = async {
                packageManager.getInstalledApplications(0)
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

            selectorFragment.setItems(apps.await())

            progress.visibility = View.GONE
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

        callback?.onImmersiveResult(ArrayList(checked))
    }
}