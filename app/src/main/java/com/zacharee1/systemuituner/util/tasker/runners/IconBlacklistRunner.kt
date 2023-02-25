package com.zacharee1.systemuituner.util.tasker.runners

import android.content.Context
import com.joaomgcd.taskerpluginlibrary.action.TaskerPluginRunnerActionNoOutput
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResult
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultSucess
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.data.tasker.TaskerIconBlacklistData
import com.zacharee1.systemuituner.util.prefManager
import com.zacharee1.systemuituner.util.writeSetting
import kotlinx.coroutines.runBlocking

class IconBlacklistRunner : TaskerPluginRunnerActionNoOutput<TaskerIconBlacklistData>() {
    override fun run(context: Context, input: TaskerInput<TaskerIconBlacklistData>): TaskerPluginResult<Unit> {
        val blacklisted = context.prefManager.blacklistedItems
        val data = input.regular

        if (data.isRemove) blacklisted.remove(data.key)
        else blacklisted.add(data.key)

        context.prefManager.blacklistedItems = blacklisted

        runBlocking {
            context.writeSetting(SettingsType.SECURE, "icon_blacklist", blacklisted.joinToString(","))
        }

        return TaskerPluginResultSucess()
    }
}