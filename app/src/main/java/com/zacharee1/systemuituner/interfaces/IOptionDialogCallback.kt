package com.zacharee1.systemuituner.interfaces

interface IOptionDialogCallback {
    var callback: ((data: Any?) -> Unit)?
}