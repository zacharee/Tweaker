package com.zacharee1.systemuituner.util

import com.topjohnwu.superuser.Shell

val hasRoot: Boolean
    get() = Shell.isAppGrantedRoot() == null && Shell.getShell().isRoot
