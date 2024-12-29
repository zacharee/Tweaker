package com.zacharee1.systemuituner.util

import com.topjohnwu.superuser.Shell

val hasRoot: Boolean
    get() = try {
        Shell.isAppGrantedRoot() == null && Shell.getShell().isRoot
    } catch (e: Throwable) {
        false
    }
