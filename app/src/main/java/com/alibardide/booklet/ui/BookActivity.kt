package com.alibardide.booklet.ui

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.alibardide.booklet.R
import com.alibardide.booklet.data.local.db.AppDatabase
import com.alibardide.booklet.data.local.db.dao.BookDao
import com.alibardide.booklet.data.model.Book
import com.alibardide.booklet.data.repository.AppRepository
import kotlinx.android.synthetic.main.activity_book.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class BookActivity : AppCompatActivity() {

    private val RESULT_LOAD_IMAGE = 101
    private var hasImage = false
    private var bitmap: Bitmap? = null
    private val states = arrayListOf<String>("Wish list", "Reading", "Finished")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book)

        bookImage.setOnClickListener {
            if (hasImage)
                imagePicker()
            else
                loadImage()
        }
        bookCancel.setOnClickListener {
            onBackPressed()
        }
        bookSave.setOnClickListener {
            saveBook()
        }

        val adapter =
            object: ArrayAdapter<String>(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                states)
            {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent) as TextView
                v.textSize = 15f
                return v
            }
        }
        bookStateSpinner.adapter = adapter
        bookStateSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                when (bookStateSpinner.selectedItemPosition) {
                    0 -> {
                        bookTotalPages.isEnabled = false
                        bookReadPages.isEnabled = false
                    }
                    1 -> {
                        bookTotalPages.isEnabled = true
                        bookReadPages.isEnabled = true
                    }
                    2 -> {
                        bookTotalPages.isEnabled = true
                        bookReadPages.isEnabled = false
                    }
                }
            }

        }
        bookStateSpinner.setSelection(1)
    }
    private fun imagePicker() {
        val alert = AlertDialog.Builder(this)
        val arrayAdapter =
            object: ArrayAdapter<String>(this, android.R.layout.select_dialog_item) {
                override fun getView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
                ): View {
                    val v = super.getView(position, convertView, parent)
                    v as TextView
                    v.textSize = 15f
                    return v
                }
            }
        arrayAdapter.add("New Image")
        arrayAdapter.add("Delete Image")

        alert.setAdapter(arrayAdapter) { _: DialogInterface, i: Int ->
            when (i) {
                0 -> loadImage()
                1 -> {
                    hasImage = false
                    bitmap = null
                    bookImage.setImageResource(R.drawable.add_pic)
                }
            }

        }

        alert.create()
            .show()
    }
    private fun loadImage() {
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.INTERNAL_CONTENT_URI
        )
        startActivityForResult(intent, RESULT_LOAD_IMAGE)
    }
    private fun saveBitmap() : String? {
        val state = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED != state) return null
        if (bitmap == null) return null
        val name = bitmap?.hash()

        try {
            val file = File(getExternalFilesDir(null), "$name.png")
            val out = FileOutputStream(file)

            bitmap?.compress(Bitmap.CompressFormat.PNG, 80, out)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return name
    }
    private fun saveBook() {
        if (!hasFullData()) return
        val totalPages = bookTotalPages.text.toString()
        val readPages = bookReadPages.text.toString()
        val name = if (hasImage)  saveBitmap() else null
        val book = Book(
            0,
            bookName.text.toString(),
            bookAuthorName.text.toString(),
            bookPublisher.text.toString(),
            bookDescription.text.toString(),
            bookCategory.text.toString(),
            bookDate.text.toString(),
            if (bookTotalPages.isEnabled) totalPages.toInt() else 0,
            if (bookReadPages.isEnabled) readPages.toInt() else 0,
            bookStateSpinner.selectedItemPosition,
            if (name != null) saveBitmap() + ".png" else ""
        )
        val database = AppDatabase(this)
        val bookDao = BookDao(database)
        val repository = AppRepository(bookDao)
        repository.saveBook(book)
        finish()
    }
    private fun hasFullData() : Boolean {
        return true
    }

    private fun Bitmap.hash(): String {
        val baos = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.WEBP, 80, baos)
        val bitmapBytes = baos.toByteArray()
        return bitmapBytes.toString()
    }

    override fun onBackPressed() {
        val alert = AlertDialog.Builder(this)
            .setMessage("Do you wanna discard this book?!")
            .setPositiveButton("Discard") { _: DialogInterface, _: Int ->
                super.onBackPressed()
            }
            .setNegativeButton("Keep", null)
        alert.create()
            .show()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK) {
            val image = data?.data!!
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, image)
            bookImage.setImageBitmap(bitmap)
            hasImage = true
        }
    }
}