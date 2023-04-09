package com.zacharee1.systemuituner.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

fun String.hexToColorIntOrNull(): Int? {
    return try {
        hexToColorInt()
    } catch (e: IllegalArgumentException) {
        null
    }
}

fun String.hexToColorOrNull(): Color? {
    return try {
        hexToColor()
    } catch (e: IllegalArgumentException) {
        null
    }
}

@Throws(IllegalArgumentException::class)
fun String.hexToColorInt(): Int {
    if (isEmpty()) {
        throw IllegalArgumentException("String must not be empty!")
    }

    return android.graphics.Color.parseColor(this)
}

@Throws(IllegalArgumentException::class)
fun String.hexToColor(): Color {
    return Color(hexToColorInt())
}

/**
 * Returns an integer array for all color channels value.
 */
fun Color.argb(): Array<Int> {
    val argb = toArgb()
    val alpha = argb shr 24 and 0xff
    val red = argb shr 16 and 0xff
    val green = argb shr 8 and 0xff
    val blue = argb and 0xff
    return arrayOf(alpha, red, green, blue)
}

/**
 * Returns ARGB color as a hex string.
 * @param hexPrefix Add # char before the hex number.
 * @param includeAlpha Include the alpha value within the hex string.
 */
fun Color.toHex(hexPrefix: Boolean = false, includeAlpha: Boolean = true): String {
    val (alpha, red, green, blue) = argb()
    return buildString {
        if (hexPrefix) {
            append("#")
        }
        if (includeAlpha) {
            append(alpha.toHex())
        }
        append(red.toHex())
        append(green.toHex())
        append(blue.toHex())
    }
}

fun Int.toHex(): String {
    return Integer.toHexString(this).let {
        if (it.length == 1) {
            "0$it"
        } else {
            it
        }
    }
}
