package com.zacharee1.systemuituner.util

import android.content.Context
import com.zacharee1.systemuituner.util.persistence.BasePersistenceHandler
import com.zacharee1.systemuituner.util.persistence.BlacklistPersistenceHandler

object PersistenceHandlerRegistry {
    val handlers = ArrayList<BasePersistenceHandler<*>>()

    fun register(context: Context) {
        handlers.clear()
        handlers.add(BlacklistPersistenceHandler(context))
    }
}