package com.zacharee1.systemuituner.activities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.data.QSTileInfo
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.databinding.ActivityQsEditorBinding
import com.zacharee1.systemuituner.databinding.QsTileBinding
import com.zacharee1.systemuituner.dialogs.AddQSTileDialog
import com.zacharee1.systemuituner.dialogs.RoundedBottomSheetDialog
import com.zacharee1.systemuituner.util.*
import com.zacharee1.systemuituner.views.GridAutofitLayoutManager
import java.util.*
import kotlin.math.max

class QSEditorActivity : AppCompatActivity() {
    private val adapter by lazy { QSEditorAdapter(this) }
    private val binding by lazy { ActivityQsEditorBinding.inflate(layoutInflater) }

    private val touchHelperCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN or ItemTouchHelper.UP or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, 0) {
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            adapter.move(viewHolder.bindingAdapterPosition, target.bindingAdapterPosition)
            return true
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)

            viewHolder as QSEditorAdapter.QSVH?

            viewHolder?.apply {
                showRemove = !showRemove
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        fun updateLayout() {
            val fullWidth = binding.root.width
            val sidePadding = max(0f, (fullWidth - dpAsPx(800)) / 2f).toInt()

            binding.qsList.setPaddingRelative(
                sidePadding, 0,
                sidePadding, 0
            )

            binding.qsList.layoutManager = GridAutofitLayoutManager(this, dpAsPx(130))
        }

        updateLayout()

        binding.root.addOnLayoutChangeListener { _, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom) {
                updateLayout()
            }
        }

        binding.qsList.adapter = adapter

        ItemTouchHelper(touchHelperCallback).attachToRecyclerView(binding.qsList)

        adapter.populateTiles()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_qs_editor, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add -> {
                AddQSTileDialog(this, adapter)
                    .show()
                true
            }
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        adapter.saveTiles()
    }

    class QSEditorAdapter(private val context: Context) : RecyclerView.Adapter<QSEditorAdapter.QSVH>() {
        private val defaultTiles = ArrayList<String>().apply {
            try {
                val remRes = context.packageManager.getResourcesForApplication("com.android.systemui")
                val id = remRes.getIdentifier("quick_settings_tiles_default", "string", "com.android.systemui")
                val amazonId = try {
                    remRes.getIdentifier("amazon_quick_settings_tiles_default", "string", "com.android.systemui")
                } catch (e: Exception) {
                    0
                }
                val samsungId = try {
                    remRes.getIdentifier("sec_quick_settings_tiles_default", "string", "com.android.systemui")
                } catch (e: Exception) {
                    0
                }

                val items = when {
                    amazonId != 0 -> {
                        //Fire tablets have a lot of different default lists, so we're just
                        //going to add them manually here.
                        "wifi,bt,airplane,moonlight,privacy,home,dnd,smarthome,camera,lowpower,rotation,exitkft"
                    }
                    samsungId != 0 -> {
                        val result = remRes.getString(samsungId)
                        val tiles = result.split(",").toMutableList()

                        remRes.getString(remRes.getIdentifier("quick_settings_custom_tile_component_names", "string", "com.android.systemui"))
                            .split(",")
                            .forEach { item ->
                                val (key, _) = item.split(":")

                                tiles.remove(key)
                            }

                        tiles.joinToString(",")
                    }
                    else -> {
                        remRes.getString(id)
                    }
                }

                addAll(items.split(","))

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                    && (!contains("wifi") && !contains("cell") && contains("internet"))) {
                    add("wifi")
                    add("cell")
                }
            } catch (_: Exception) {}
        }

        private val customTiles = ArrayList<String>().apply {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                val customTiles = context.packageManager
                    .queryIntentServices(Intent("android.service.quicksettings.action.QS_TILE"), PackageManager.MATCH_ALL)

                customTiles?.forEach {
                    add("custom(${it.componentInfo.component.flattenToString()})")
                }
            }
        }

        private val currentTiles = ArrayList<QSTileInfo>()
        val availableTiles = ArrayList<String>()

        fun populateTiles() {
            currentTiles.clear()

            val tiles = context.getSetting(SettingsType.SECURE, "sysui_qs_tiles")

            if (tiles.isNullOrBlank()) currentTiles.addAll(defaultTiles.map { QSTileInfo(it) })
            else {
                currentTiles.addAll(
                    tiles.split(",").map { QSTileInfo(it) }
                )
            }

            updateAvailableTiles()
        }

        fun move(from: Int, to: Int) {
            if (from < to) {
                for (i in from until to) {
                    Collections.swap(currentTiles, i, i + 1)
                }
            } else {
                for (i in from downTo to + 1) {
                    Collections.swap(currentTiles, i, i - 1)
                }
            }
            notifyItemMoved(from, to)
        }

        fun addTile(tile: QSTileInfo) {
            currentTiles.add(tile)
            notifyItemInserted(currentTiles.lastIndex)

            updateAvailableTiles()
        }

        private fun removeTile(position: Int) {
            currentTiles.removeAt(position)
            notifyItemRemoved(position)

            updateAvailableTiles()
        }

        private fun updateAvailableTiles() {
            availableTiles.clear()

            availableTiles.addAll(defaultTiles.filterNot {
                currentTiles.map { tile -> tile.key }.contains(it)
            })

            availableTiles.addAll(customTiles.filterNot {
                currentTiles.map { tile -> tile.key }.contains(it)
            })
        }

        fun saveTiles() {
            val tileString = currentTiles.joinToString(",") { it.key }

            context.prefManager.saveOption(SettingsType.SECURE, "sysui_qs_tiles", tileString)
            context.writeSecure("sysui_qs_tiles", tileString)
        }

        override fun getItemCount(): Int {
            return currentTiles.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QSVH {
            return QSVH(
                LayoutInflater.from(parent.context).inflate(R.layout.qs_tile, parent, false)
            )
        }

        override fun onBindViewHolder(holder: QSVH, position: Int) {
            holder.onBind(currentTiles[position])
        }

        inner class QSVH(view: View) : RecyclerView.ViewHolder(view) {
            var showRemove: Boolean
                get() = vhBinding.remove.isVisible
                set(value) {
                    vhBinding.remove.isVisible = value
                }

            private val vhBinding = QsTileBinding.bind(itemView)

            init {
//                vhBinding.clickTarget.setOnLongClickListener {
//                    vhBinding.remove.apply { isVisible = !isVisible }
//                    true
//                }

                vhBinding.remove.setOnClickListener {
                    val newPos = bindingAdapterPosition

                    if (newPos != -1) {
                        removeTile(newPos)
                        vhBinding.remove.isVisible = false
                    }
                }
            }

            fun onBind(info: QSTileInfo) {
                vhBinding.clickTarget.setOnClickListener {
                    if (info.type == QSTileInfo.Type.CUSTOM || info.type == QSTileInfo.Type.INTENT) {
                        RoundedBottomSheetDialog(context).apply {
                            setIcon(info.getIcon(context))
                            setTitle(info.getLabel(context))

                            setMessage(
                                info.key
                            )

                            setPositiveButton(android.R.string.ok, null)

                            show()
                        }
                    }
                }

                vhBinding.qsTileIcon.setImageDrawable(info.getIcon(itemView.context))
                vhBinding.label.text = info.getLabel(itemView.context)
                vhBinding.qsTileType.setText(
                    when (info.type) {
                        QSTileInfo.Type.CUSTOM -> R.string.tile_custom
                        QSTileInfo.Type.INTENT -> R.string.intent
                        QSTileInfo.Type.STANDARD -> R.string.snooze_default
                    }
                )
            }
        }
    }
}