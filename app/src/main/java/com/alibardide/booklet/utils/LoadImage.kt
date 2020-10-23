package com.alibardide.booklet.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.widget.ImageView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import java.io.FileInputStream

class LoadImage(
    private val context: Context,
    private val image: ImageView,
    private val url: String
) : AsyncTask<String, Void, Drawable?>() {

    override fun doInBackground(vararg p0: String?): Drawable? {
        val path = context.getExternalFilesDir(null).toString() + "/" + url
        val inputStream = FileInputStream(path)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val drawable = RoundedBitmapDrawableFactory.create(context.resources, bitmap)
        drawable.isCircular = true
        drawable.cornerRadius = 20f
        return drawable
    }

    override fun onPostExecute(result: Drawable?) {
        image.setImageDrawable(result)
    }
}