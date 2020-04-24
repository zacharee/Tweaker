package com.zacharee1.systemuituner.data.tasker

import com.joaomgcd.taskerpluginlibrary.input.TaskerInputField
import com.joaomgcd.taskerpluginlibrary.input.TaskerInputRoot
import com.zacharee1.systemuituner.util.SettingsType

@TaskerInputRoot
class TaskerWriteSettingData @JvmOverloads constructor(
    @field:TaskerInputField("type") var type: SettingsType = SettingsType.UNDEFINED,
    @field:TaskerInputField("key") var key: String = "",
    @field:TaskerInputField("value") var value: String? = null
)