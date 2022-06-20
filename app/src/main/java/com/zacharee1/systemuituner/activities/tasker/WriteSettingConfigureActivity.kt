package com.zacharee1.systemuituner.activities.tasker

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfig
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.zacharee1.systemuituner.data.tasker.TaskerWriteSettingData
import com.zacharee1.systemuituner.databinding.TaskerWriteSettingBinding
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.util.tasker.helpers.WriteSettingHelper

class WriteSettingConfigureActivity : AppCompatActivity(), TaskerPluginConfig<TaskerWriteSettingData> {
    override val context: Context
        get() = this
    override val inputForTasker: TaskerInput<TaskerWriteSettingData>
        get() = TaskerInput(TaskerWriteSettingData(type, key, value))

    private var type: String = SettingsType.UNDEFINED.toString()
    private var key: String = ""
    private var value: String? = null

    private val helper by lazy { WriteSettingHelper(this) }
    private val binding by lazy { TaskerWriteSettingBinding.inflate(layoutInflater) }

    override fun assignFromInput(input: TaskerInput<TaskerWriteSettingData>) {
        type = input.regular.type
        key = input.regular.key
        value = input.regular.value
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        helper.onCreate()

        setContentView(binding.root)

        binding.keyEntry.setText(key)
        binding.valueEntry.setText(value)

        val parsedType = SettingsType.fromString(type).value
        if (parsedType != -1) {
            binding.settingsType.setSelection(parsedType)
        }

        binding.apply.setOnClickListener {
            key = binding.keyEntry.text?.toString() ?: ""
            value = binding.valueEntry.text?.toString()
            type = binding.settingsType.selectedItem.toString()

            helper.finishForTasker()
        }
    }
}