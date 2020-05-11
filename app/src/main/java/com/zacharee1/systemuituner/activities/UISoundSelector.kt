package com.zacharee1.systemuituner.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zacharee1.systemuituner.IUISoundSelectionCallback
import com.zacharee1.systemuituner.util.callSafely
import java.io.File

class UISoundSelector : AppCompatActivity() {
    companion object {
        private const val REQ_SELECTION = 100

        private const val EXTRA_KEY = "key"
        private const val EXTRA_CALLBACK = "callback"

        fun start(context: Context, key: String, callback: IUISoundSelectionCallback) {
            val activity = Intent(context, UISoundSelector::class.java)
            activity.putExtra(EXTRA_KEY, key)
            activity.putExtra(EXTRA_CALLBACK, Bundle().apply { putBinder(EXTRA_CALLBACK, callback.asBinder()) })

            context.startActivity(activity)
        }
    }

    private val key by lazy { intent.getStringExtra(EXTRA_KEY) }
    private val callback by lazy {
        val binder = intent.getBundleExtra(EXTRA_CALLBACK)?.getBinder(EXTRA_CALLBACK)
        if (binder != null) {
            IUISoundSelectionCallback.Stub.asInterface(binder)
        } else null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val docIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        docIntent.type = "audio/*"

        startActivityForResult(docIntent, REQ_SELECTION)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQ_SELECTION && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val folder = File(getExternalFilesDir(null), "sounds")
                folder.mkdirs()

                val dest = File(folder, "ui_sound_$key")
                if (dest.exists()) dest.delete()
                dest.createNewFile()

                dest.outputStream().use { output ->
                    contentResolver.openInputStream(uri).use { input ->
                        input.copyTo(output)
                    }
                }

                callback?.callSafely {
                    it.onSoundSelected(dest.absolutePath, key)
                }
            }
        }

        finish()
    }
}