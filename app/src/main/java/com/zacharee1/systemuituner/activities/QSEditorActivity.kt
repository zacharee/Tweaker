package com.zacharee1.systemuituner.activities

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.data.QSTileInfo
import com.zacharee1.systemuituner.util.SettingsType
import com.zacharee1.systemuituner.util.getSetting
import kotlinx.android.synthetic.main.qs_tile.view.*

class QSEditorActivity : AppCompatActivity() {
    private val gridManager by lazy { GridLayoutManager(this, 3) }

    class QSEditorAdapter(private val context: Context) : RecyclerView.Adapter<QSEditorAdapter.QSVH>() {
        private val defaultTiles = ArrayList<String>().apply {
            try {
                val remRes = context.packageManager.getResourcesForApplication("com.android.systemui")
                val id = remRes.getIdentifier("quick_settings_tiles_default", "string", "com.android.systemui")

                val items = remRes.getString(id)

                addAll(items.split(","))
            } catch (e: Exception) {}
        }

        val currentTiles = ArrayList<QSTileInfo>()
        private val availableTiles = ArrayList<String>()

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

        fun updateAvailableTiles() {
            availableTiles.clear()

            availableTiles.addAll(defaultTiles.filterNot {
                currentTiles.map { tile -> tile.key }.contains(it)
            })
        }

        override fun getItemCount(): Int {
            return currentTiles.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QSVH {
            return QSVH(
                LayoutInflater.from(parent.context).inflate(R.layout.qs_tile, parent, false)
            )
        }

        @ExperimentalStdlibApi
        override fun onBindViewHolder(holder: QSVH, position: Int) {
            holder.onBind(currentTiles[position])
        }

        inner class QSVH(view: View) : RecyclerView.ViewHolder(view) {
            @ExperimentalStdlibApi
            fun onBind(info: QSTileInfo) {
                itemView.icon.setImageDrawable(info.getIcon(itemView.context))
                itemView.label.text = info.getLabel(itemView.context)
            }
        }
    }
}