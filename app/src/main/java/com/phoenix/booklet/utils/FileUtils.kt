package com.phoenix.booklet.utils

import android.content.Context
import android.net.Uri
import com.phoenix.booklet.data.FileResult
import java.io.File
import java.io.FileOutputStream

fun saveUriAsPhoto(context: Context, uri: Uri?, name: String): FileResult {
    if (uri == null)
        return FileResult.Error("Uri is null the fuck am I gonna save?")
    val file = File(context.filesDir, "$name.png")
    val stream = FileOutputStream(file)

    try {
        stream.write(uri.toString().toByteArray())
        stream.close()
        return FileResult.Success("${context.filesDir}/$name.png")
    } catch (e: Exception) {
        e.printStackTrace()
        return FileResult.Error(e.message)
    }
}

fun getUriFromFile(path: String?): Uri? {
    if (path == null)
        return null
    val file = File(path)
    return Uri.fromFile(file)
}

fun deleteFileFromPath(path: String?) {
    if (path == null)
        return
    val file = File(path)
    if (file.exists())
        file.delete()
}