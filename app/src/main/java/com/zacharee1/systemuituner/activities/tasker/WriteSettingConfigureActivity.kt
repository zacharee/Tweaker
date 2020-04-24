package com.zacharee1.systemuituner.activities.tasker

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfig
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.data.tasker.TaskerWriteSettingData
import com.zacharee1.systemuituner.util.SettingsType
import com.zacharee1.systemuituner.util.tasker.helpers.WriteSettingHelper
import kotlinx.android.synthetic.main.tasker_write_setting.*

class WriteSettingConfigureActivity : AppCompatActivity(), TaskerPluginConfig<TaskerWriteSettingData> {
    override val context: Context
        get() = this
    override val inputForTasker: TaskerInput<TaskerWriteSettingData>
        get() = TaskerInput(TaskerWriteSettingData(type, key, value))

    private var type: String = SettingsType.UNDEFINED.toString()
    private var key: String = ""
    private var value: String? = null

    private val helper by lazy { WriteSettingHelper(this) }

    override fun assignFromInput(input: TaskerInput<TaskerWriteSettingData>) {
        type = input.regular.type
        key = input.regular.key
        value = input.regular.value
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        helper.onCreate()

        setContentView(R.layout.tasker_write_setting)

        key_entry.setText(key)
        value_entry.setText(value)

        val parsedType = SettingsType.fromString(type).value
        if (parsedType != -1) {
            settings_type.setSelection(parsedType)
        }

        apply.setOnClickListener {
            key = key_entry.text?.toString() ?: ""
            value = value_entry.text?.toString()
            type = settings_type.selectedItem.toString()

            helper.finishForTasker()
        }
    }
}