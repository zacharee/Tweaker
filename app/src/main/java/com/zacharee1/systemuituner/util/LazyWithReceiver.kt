package com.zacharee1.systemuituner.util

import java.io.Serializable
import kotlin.reflect.KProperty

fun <Receiver, T> lazy(
    lock: Any? = null,
    initializer: Receiver.() -> T
) = LazyWithReceiver(initializer, lock)

@Suppress("ClassName")
private object UNINITIALIZED_VALUE

class LazyWithReceiver<in Receiver, out T>(
    initializer: Receiver.() -> T,
    lock: Any? = null
): Serializable {
    private var initializer: (Receiver.() -> T)? = initializer
    @Volatile private var _value: Any? = UNINITIALIZED_VALUE

    private val lock = lock ?: this

    fun isInitialized(): Boolean {
        return _value !== UNINITIALIZED_VALUE
    }

    override fun toString(): String {
        return if (isInitialized()) _value.toString() else "Uninitialized Lazy $this"
    }

    @Suppress("LocalVariableName", "UNCHECKED_CAST")
    operator fun getValue(thisRef: Receiver, property: KProperty<*>): T {
        val _v1 = _value

        if (_v1 !== UNINITIALIZED_VALUE) {
            return _v1 as T
        }

        return synchronized(lock) {
            val _v2 = _value
            if (_v2 !== UNINITIALIZED_VALUE) {
                _v2 as T
            } else {
                val typedValue = initializer!!(thisRef)
                _value = typedValue
                initializer = null
                typedValue
            }
        }
    }
}
