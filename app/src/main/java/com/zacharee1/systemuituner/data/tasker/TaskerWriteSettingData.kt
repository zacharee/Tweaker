package com.zacharee1.systemuituner.data.tasker

import com.joaomgcd.taskerpluginlibrary.input.TaskerInputField
import com.joaomgcd.taskerpluginlibrary.input.TaskerInputRoot
import com.zacharee1.systemuituner.data.SettingsType

@TaskerInputRoot
class TaskerWriteSettingData @JvmOverloads constructor(
    @field:TaskerInputField("type") var type: String = SettingsType.UNDEFINED.toString(),
    @field:TaskerInputField("key") var key: String = "",
    @field:TaskerInputField("value") var value: String? = null
)