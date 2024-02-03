package com.zacharee1.systemuituner.util

import com.bugsnag.android.BreadcrumbType
import com.bugsnag.android.Bugsnag

object BugsnagUtils {
    fun notify(exception: Throwable) {
        if (Bugsnag.isStarted()) {
            Bugsnag.notify(exception)
        }
    }

    fun leaveBreadcrumb(message: String) {
        if (Bugsnag.isStarted()) {
            Bugsnag.leaveBreadcrumb(message)
        }
    }

    fun leaveBreadcrumb(message: String, metadata: Map<String, Any>, type: BreadcrumbType) {
        if (Bugsnag.isStarted()) {
            Bugsnag.leaveBreadcrumb(message, metadata, type)
        }
    }
}
