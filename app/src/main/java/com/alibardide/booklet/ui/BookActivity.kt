package com.alibardide.booklet.ui

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.alibardide.booklet.R
import com.alibardide.booklet.data.local.db.AppDatabase
import com.alibardide.booklet.data.local.db.dao.BookDao
import com.alibardide.booklet.data.model.Book
import com.alibardide.booklet.data.repository.AppRepository
import com.alibardide.booklet.utils.FileUtil
import com.alibardide.booklet.utils.LoadImage
import com.google.android.material.snackbar.Snackbar
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import id.zelory.compressor.loadBitmap
import kotlinx.android.synthetic.main.activity_book.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.*
import java.util.*

class BookActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private val RESULT_LOAD_IMAGE = 101
    private var book: Book? = null
    private var hasImage = false
    private var imageFile: File? = null
    private var compressedImage: File? = null
    private val states = arrayListOf<String>("Wish list", "Reading", "Finished")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book)

        init()
        val data = intent.getSerializableExtra("book") as Book?
        if (data != null) initData(data)
    }

    private fun init() {
        bookImage.setOnClickListener {
            val listener = object: PermissionListener {
                override fun onPermissionGranted() {
                    if (hasImage)
                        imagePicker()
                    else
                        selectImage()
                }
                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    snackBar("Permission Denied")
                }

            }

            TedPermission.with(this)
                .setPermissionListener(listener)
                .setPermissions(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check()
        }
        bookCancel.setOnClickListener {
            onBackPressed()
        }
        bookSave.setOnClickListener {
            saveBook()
        }

        val now = Calendar.getInstance()
        bookDate.text =
            formatDate(now[Calendar.YEAR], now[Calendar.MONTH], now[Calendar.DAY_OF_MONTH])
        bookDate.setOnClickListener {
            val dpd =
                DatePickerDialog.newInstance(
                    this,
                    now[Calendar.YEAR],
                    now[Calendar.MONTH],
                    now[Calendar.DAY_OF_MONTH]
                )
            dpd.show(supportFragmentManager, "Date Picker")
        }

        val adapter = object: ArrayAdapter<String>(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                states) {
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
                        bookTotalPages.removeError()
                        bookReadPages.isEnabled = false
                        bookReadPages.removeError()
                    }
                    1 -> {
                        bookTotalPages.isEnabled = true
                        bookTotalPages.initError()
                        bookReadPages.isEnabled = true
                        bookReadPages.initError()
                    }
                    2 -> {
                        bookTotalPages.isEnabled = true
                        bookTotalPages.initError()
                        bookReadPages.isEnabled = false
                        bookReadPages.removeError()
                    }
                }
            }

        }
        bookStateSpinner.setSelection(1)

        bookName.initError()
        bookAuthorName.initError()
        bookCategory.initError()
        bookTotalPages.initError()
        bookReadPages.initError()
    }
    private fun initData(book: Book) {
        this.book = book
        if (book.picLocation != "") {
            hasImage = true
            LoadImage(this, bookImage, book.picLocation).execute()
        }
        bookName.setText(book.name)
        bookAuthorName.setText(book.author)
        bookPublisher.setText(book.publisher)
        bookCategory.setText(book.category)
        bookDate.text = book.date
        bookStateSpinner.setSelection(book.state)
        bookTotalPages.setText(book.pages.toString())
        bookReadPages.setText(book.progress.toString())
        bookDescription.setText(book.description)
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
                0 -> selectImage()
                1 -> {
                    hasImage = false
                    imageFile = null
                    bookImage.setImageResource(R.drawable.add_pic)
                }
            }

        }

        alert.create()
            .show()
    }
    private fun selectImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, RESULT_LOAD_IMAGE)
    }
    private fun deleteImage(path: String) {
        val file = File(path)
        file.delete()
        if (file.exists()) {
            file.canonicalFile.delete()
            if (file.exists()) deleteFile(file.name)
        }
    }
    private fun saveBitmap(file: File?) : String? {
        var path: String? = null
        try {
            val sFile = File(getExternalFilesDir(null), "${file.hashCode()}")
            sFile.outputStream().use {
                val bitmap = BitmapFactory.decodeFile(file?.absolutePath)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, it)
                path = sFile.absolutePath
            }
        } catch (e: IOException) {
            Log.e("BitmapCompressU", e.toString())
        }
        return path
    }
    private fun saveBook() {
        if (!hasFullData()) {
            snackBar("Please fill out the fields")
            return
        }
        val totalPages = bookTotalPages.text.toString()
        val readPages = bookReadPages.text.toString()
        if (totalPages.isNotEmpty() && readPages.isNotEmpty()) {
            if (!isNumbersValid(totalPages.toInt(), readPages.toInt())) return
        }
        GlobalScope.launch {
            val name = if (hasImage) {
                when {
                    imageFile != null -> {
                        compressedImage =
                            Compressor.compress(this@BookActivity, imageFile!!)
                        saveBitmap(compressedImage)
                    }
                    book != null -> book?.picLocation
                    else -> null
                }
            } else null
            val now = Calendar.getInstance()
            val date =
                formatDate(now[Calendar.YEAR], now[Calendar.MONTH], now[Calendar.DAY_OF_MONTH])
            val newBook = Book(
                book?.id ?: 0,
                bookName.text.toString(),
                bookAuthorName.text.toString(),
                bookPublisher.text.toString(),
                bookDescription.text.toString(),
                bookCategory.text.toString(),
                bookDate.text.toString(),
                if (bookTotalPages.isEnabled) totalPages.toInt() else 0,
                if (bookReadPages.isEnabled) readPages.toInt() else 0,
                bookStateSpinner.selectedItemPosition,
                name ?: "",
                book?.createdOn ?: date,
                date
            )
            if (book != null && !hasImage && book?.picLocation != "")
                deleteImage(book!!.picLocation)
            val database = AppDatabase(this@BookActivity)
            val bookDao = BookDao(database)
            val repository = AppRepository(bookDao)
            if (book != null) repository.updateBook(book?.id.toString(), newBook)
            else repository.saveBook(newBook)
            finish()
        }
    }
    private fun hasFullData() : Boolean {
        var result = true
        if (bookName.text.toString() == "") {
            bookName.setError()
            result = false
        }
        if (bookAuthorName.text.toString() == "") {
            bookAuthorName.setError()
            result = false
        }
        if (bookCategory.text.toString() == "") {
            bookCategory.setError()
            result = false
        }
        if (bookTotalPages.isEnabled && bookTotalPages.text.toString() == "") {
            bookTotalPages.setError()
            result = false
        }
        if (bookReadPages.isEnabled && bookReadPages.text.toString() == "") {
            bookReadPages.setError()
            result = false
        }

        return result
    }
    private fun isNumbersValid(pages: Int, progress: Int) : Boolean {
        var result = true
        when {
            pages < progress -> {
                bookReadPages.setError()
                snackBar("Read pages can\'t be grater than total pages")
                result = false
            }
            pages == 0 -> {
                bookTotalPages.setError()
                snackBar("Number values can\'t be 0")
                result = false
            }
            progress == 0 -> {
                bookReadPages.setError()
                snackBar("Number values can\'t be 0")
                result = false
            }
        }
       return result
    }
    private fun formatDate(year: Int, monthOfYear: Int, dayOfMonth: Int) : String {
        return "$year/" +
                (if (monthOfYear + 1 < 10) "0${monthOfYear + 1}" else monthOfYear + 1) + "/" +
                (if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth)
    }
    private fun snackBar(message: String) {
        Snackbar.make(edtbookParent, message, Snackbar.LENGTH_LONG).show()
    }

    private fun EditText.initError() {
        addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0 == "")
                    setError()
                else
                    removeError()
            }

        })
    }
    private fun EditText.setError() {
        background.setColorFilter(resources.getColor(R.color.colorRed), PorterDuff.Mode.SRC_ATOP)
    }
    private fun EditText.removeError() { background.clearColorFilter() }

    override fun onBackPressed() {
        val alert = AlertDialog.Builder(this)
            .setMessage("Do you wanna discard ${if (book != null) "changes" else "this book"}?!")
            .setPositiveButton("Discard") { _: DialogInterface, _: Int ->
                super.onBackPressed()
            }
            .setNegativeButton("Keep", null)
        val dialog = alert.create()
        dialog.setOnShowListener {
                val positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                positive.setTextColor(resources.getColor(R.color.colorRed))
            }
        dialog.show()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                snackBar("Failed to open picture!")
                return
            }
            try {
                imageFile = FileUtil.from(this, data.data!!).also {
                    bookImage.setImageBitmap(loadBitmap(it))
                    hasImage = true
                }
            } catch (e: IOException) {
                snackBar("Failed to read picture data!")
                e.printStackTrace()
            }
        }
    }
    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        bookDate.text = formatDate(year, monthOfYear, dayOfMonth)
    }
}