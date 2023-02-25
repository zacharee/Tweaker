package com.zacharee1.systemuituner.util.tasker.runners

import android.content.Context
import com.joaomgcd.taskerpluginlibrary.action.TaskerPluginRunnerActionNoOutput
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResult
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultError
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultSucess
import com.zacharee1.systemuituner.data.tasker.TaskerWriteSettingData
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.util.writeSetting
import kotlinx.coroutines.runBlocking

class WriteSettingRunner : TaskerPluginRunnerActionNoOutput<TaskerWriteSettingData>() {
    override fun run(context: Context, input: TaskerInput<TaskerWriteSettingData>): TaskerPluginResult<Unit> {
        val type = input.regular.type
        val key = input.regular.key
        val value = input.regular.value

        val sType = SettingsType.fromString(type)

        return runBlocking {
            if (context.writeSetting(sType, key, value, true)) {
                TaskerPluginResultSucess()
            } else {
                TaskerPluginResultError(100, "Unable to save $key to $type settings with value $value")
            }
        }
    }
}