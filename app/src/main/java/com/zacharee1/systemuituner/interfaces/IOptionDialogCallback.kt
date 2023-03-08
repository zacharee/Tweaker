package com.zacharee1.systemuituner.interfaces

interface IOptionDialogCallback {
    var callback: (suspend (data: Any?) -> Boolean)?
}