package com.zacharee1.systemuituner.util

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URLConnection

// Sourced from
// https://github.com/commonsguy/cw-omnibus/blob/master/ContentProvider/Files/app/src/main/java/com/commonsware/android/cp/files/

abstract class BaseProvider : ContentProvider() {
    companion object {
        private val OPENABLE_PROJECTION = arrayOf(
            OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE
        )
    }

    protected val deviceProtectedContext: Context by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createDeviceProtectedStorageContext()
        } else {
            context
        }
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String?>?, sortOrder: String?
    ): Cursor {
        var mutableProjection = projection
        if (mutableProjection == null) {
            mutableProjection = OPENABLE_PROJECTION
        }
        val cursor = MatrixCursor(mutableProjection, 1)
        val b = cursor.newRow()
        for (col in mutableProjection) {
            if (OpenableColumns.DISPLAY_NAME == col) {
                b.add(getFileName(uri))
            } else if (OpenableColumns.SIZE == col) {
                b.add(getDataLength(uri))
            } else { // unknown, so just add null
                b.add(null)
            }
        }
        return cursor
    }

    override fun getType(uri: Uri): String? {
        return URLConnection.guessContentTypeFromName(uri.toString())
    }

    protected open fun getFileName(uri: Uri): String? {
        return uri.lastPathSegment
    }

    protected open fun getDataLength(uri: Uri?): Long {
        return AssetFileDescriptor.UNKNOWN_LENGTH
    }

    override fun insert(uri: Uri?, initialValues: ContentValues?): Uri? {
        throw RuntimeException("Operation not supported")
    }

    override fun update(
        uri: Uri?, values: ContentValues?, where: String?,
        whereArgs: Array<String?>?
    ): Int {
        throw RuntimeException("Operation not supported")
    }

    override fun delete(uri: Uri?, where: String?, whereArgs: Array<String?>?): Int {
        throw RuntimeException("Operation not supported")
    }

    @Throws(IOException::class)
    open fun copy(`in`: InputStream, dst: File?) {
        val out = FileOutputStream(dst)
        val buf = ByteArray(1024)
        var len: Int
        while (`in`.read(buf).also { len = it } >= 0) {
            out.write(buf, 0, len)
        }
        `in`.close()
        out.close()
    }
}

class SoundsProvider : BaseProvider() {
    override fun onCreate(): Boolean {
        return true
    }

    @Throws(FileNotFoundException::class)
    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {

        val root = deviceProtectedContext.filesDir
        val f = File(root, uri.path).absoluteFile
        if (!f.path.startsWith(root.path)) {
            throw SecurityException("Resolved path jumped beyond root")
        }
        if (f.exists()) {
            return ParcelFileDescriptor.open(f, parseMode(mode))
        }
        throw FileNotFoundException(uri.path)
    }

    override fun getDataLength(uri: Uri?): Long {
        if (uri == null) return 0

        val f = File(deviceProtectedContext.filesDir, uri.path)
        return f.length()
    }

    // following is from ParcelFileDescriptor source code
    // Copyright (C) 2006 The Android Open Source Project
    // (even though this method was added much after 2006...)
    private fun parseMode(mode: String): Int {
        val modeBits = when (mode) {
            "r" -> {
                ParcelFileDescriptor.MODE_READ_ONLY
            }
            "w", "wt" -> {
                (ParcelFileDescriptor.MODE_WRITE_ONLY
                        or ParcelFileDescriptor.MODE_CREATE
                        or ParcelFileDescriptor.MODE_TRUNCATE)
            }
            "wa" -> {
                (ParcelFileDescriptor.MODE_WRITE_ONLY
                        or ParcelFileDescriptor.MODE_CREATE
                        or ParcelFileDescriptor.MODE_APPEND)
            }
            "rw" -> {
                (ParcelFileDescriptor.MODE_READ_WRITE
                        or ParcelFileDescriptor.MODE_CREATE)
            }
            "rwt" -> {
                (ParcelFileDescriptor.MODE_READ_WRITE
                        or ParcelFileDescriptor.MODE_CREATE
                        or ParcelFileDescriptor.MODE_TRUNCATE)
            }
            else -> {
                throw IllegalArgumentException("Bad mode '$mode'")
            }
        }
        return modeBits
    }
}