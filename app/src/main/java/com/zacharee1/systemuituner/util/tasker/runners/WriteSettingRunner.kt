package com.zacharee1.systemuituner.util.tasker.runners

import android.content.Context
import com.joaomgcd.taskerpluginlibrary.action.TaskerPluginRunnerActionNoOutput
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResult
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultError
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultSucess
import com.zacharee1.systemuituner.data.tasker.TaskerWriteSettingData
import com.zacharee1.systemuituner.util.PersistenceHandlerRegistry
import com.zacharee1.systemuituner.util.SettingsType
import com.zacharee1.systemuituner.util.prefManager
import com.zacharee1.systemuituner.util.writeSetting

class WriteSettingRunner : TaskerPluginRunnerActionNoOutput<TaskerWriteSettingData>() {
    override fun run(context: Context, input: TaskerInput<TaskerWriteSettingData>): TaskerPluginResult<Unit> {
        val type = input.regular.type
        val key = input.regular.key
        val value = input.regular.value

        val handler = PersistenceHandlerRegistry.handlers.find { it.settingsKey == key }
        val sType = SettingsType.fromString(type)

        return if (context.writeSetting(sType, key, value)) {
            if (handler != null) {
                handler.savePreferenceValue(value)
            } else {
                context.prefManager.saveOption(sType, key, value)
            }
            TaskerPluginResultSucess()
        } else {
            TaskerPluginResultError(100, "Unable to save $key to $type settings with value $value")
        }
    }
}