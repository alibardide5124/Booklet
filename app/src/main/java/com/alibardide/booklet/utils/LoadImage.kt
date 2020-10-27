package com.alibardide.booklet.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.util.Log
import android.widget.ImageView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import java.io.FileInputStream
import java.io.IOException

class LoadImage(
    private val context: Context,
    private val image: ImageView,
    private val path: String
) : AsyncTask<String, Void, Drawable?>() {

    override fun doInBackground(vararg p0: String?): Drawable? {
        var drawable: Drawable? = null
        try {
            val inputStream = FileInputStream(path)
            drawable = RoundedBitmapDrawableFactory.create(context.resources, inputStream)
            drawable.isCircular = true
            drawable.cornerRadius = 20f
        } catch (e: IOException) {
            Log.e("Bitmap", e.toString())
        }
        return drawable
    }

    override fun onPostExecute(result: Drawable?) {
        if (result != null) image.setImageDrawable(result)
    }
}