package com.zacharee1.systemuituner.util.tasker.helpers

import com.joaomgcd.taskerpluginlibrary.SimpleResult
import com.joaomgcd.taskerpluginlibrary.SimpleResultError
import com.joaomgcd.taskerpluginlibrary.SimpleResultSuccess
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfig
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigHelperNoOutput
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.zacharee1.systemuituner.data.TaskerIconBlacklistData
import com.zacharee1.systemuituner.util.tasker.runners.IconBlacklistRunner

class IconBlacklistHelper(config: TaskerPluginConfig<TaskerIconBlacklistData>) : TaskerPluginConfigHelperNoOutput<TaskerIconBlacklistData, IconBlacklistRunner>(config) {
    override val runnerClass: Class<IconBlacklistRunner> = IconBlacklistRunner::class.java
    override val inputClass: Class<TaskerIconBlacklistData> = TaskerIconBlacklistData::class.java

    override fun isInputValid(input: TaskerInput<TaskerIconBlacklistData>): SimpleResult {
        return if (input.regular.key.isBlank()) SimpleResultError("Key must not be blank")
        else SimpleResultSuccess()
    }
}