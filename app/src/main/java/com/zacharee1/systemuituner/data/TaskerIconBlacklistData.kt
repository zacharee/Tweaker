package com.zacharee1.systemuituner.data

import com.joaomgcd.taskerpluginlibrary.input.TaskerInputField
import com.joaomgcd.taskerpluginlibrary.input.TaskerInputRoot

@TaskerInputRoot
class TaskerIconBlacklistData @JvmOverloads constructor(
    @field:TaskerInputField("isRemove") var isRemove: Boolean = false,
    @field:TaskerInputField("key") var key: String = ""
)