package com.alibardide.booklet.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.PorterDuff
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView
import com.alibardide.booklet.R
import com.alibardide.booklet.data.local.db.AppDatabase
import com.alibardide.booklet.data.local.db.dao.BookDao
import com.alibardide.booklet.data.model.Book
import com.alibardide.booklet.data.repository.AppRepository
import com.alibardide.booklet.ui.BookActivity
import com.alibardide.booklet.utils.LoadImage
import kotlinx.android.synthetic.main.activity_book.view.*
import kotlinx.android.synthetic.main.book_item.view.*
import pl.hypeapp.materialtimelineview.MaterialTimelineView
import java.io.File
import java.io.IOException

class BookViewHolder(
    private val context: Context,
    itemView: View,
    private val adapter: BookAdapter
) : RecyclerView.ViewHolder(itemView) {

    private lateinit var book: Book

    fun setData(item: Book, timelinePosition: Int) {
        book = item
        setName(item.name)
        setAuthorName(item.author)
        setCategory(item.category)
        setDate(item.date)
        setState(item.state)
        setTimeline(timelinePosition)
        setImage(item.picLocation)
    }
    private fun setName(name: String) { itemView.bookItemName.text = name }
    private fun setAuthorName(author: String) { itemView.bookItemAuthorName.text = author }
    private fun setCategory(category: String) {
        if (category != "default")
            itemView.bookItemCategory.text = category
        else
            itemView.bookItemCategory.visibility = View.GONE
    }
    private fun setDate(date: String) { itemView.bookItemDate.text = date }
    private fun setState(state: Int) {
        when (state) {
            0 -> {
                itemView.bookItemState.text = "Wish list"
                itemView.bookItemState.badgeColor(
                    ContextCompat.getColor(context, R.color.colorAccent)
                )
            }
            1 -> {
                val percent = (book.progress * 100.0f / book.pages).toInt().toString()
                itemView.bookItemState.text = "Reading $percent%"
                itemView.bookItemState.badgeColor(
                    ContextCompat.getColor(context, R.color.colorBlack)
                )
            }
            else -> {
                itemView.bookItemState.text = "Finished"
                itemView.bookItemState.badgeColor(
                    ContextCompat.getColor(context, R.color.colorGreen)
                )
            }
        }
    }
    private fun setTimeline(position: Int) {
        itemView.bookItemTimeline.position = when (position) {
            -1 -> MaterialTimelineView.POSITION_FIRST
            0 -> MaterialTimelineView.POSITION_LAST
            else -> MaterialTimelineView.POSITION_MIDDLE
        }
        itemView.setPadding(0, if (position == -1) 96 else 0, 0, if (position == 0) 96 else 0)
        itemView.bookItemTimeline.setOnClickListener {
            val intent = Intent(context, BookActivity::class.java)
            intent.putExtra("book", book)
            context.startActivity(intent)
        }
        itemView.bookItemTimeline.setOnLongClickListener {
            val alert = AlertDialog.Builder(context)
                .setTitle("Delete book")
                .setMessage("Are you sure to delete this book?!")
                .setPositiveButton("Delete") { _: DialogInterface, _: Int ->
                    val database = AppDatabase(context)
                    val bookDao = BookDao(database)
                    val repository = AppRepository(bookDao)
                    repository.deleteBook(book.id.toString())

                    if (book.picLocation != "") {
                        val file = File(book.picLocation)
                        file.delete()
                        if (file.exists()) {
                            file.canonicalFile.delete()
                            if (file.exists()) context.deleteFile(file.name)
                        }
                    }
                    adapter.refresh(repository.findAllBooks())
                }
                .setNeutralButton("Keep", null)

            val dialog = alert.create()
            dialog.setOnShowListener {
                val positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                positive.setTextColor(ContextCompat.getColor(context, R.color.colorRed))
            }
            dialog.show()
            true
        }
    }
    private fun setImage(url: String) {
        if (url != "") {
            try {
                LoadImage(context, itemView.bookItemThumb, url).execute()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else itemView.bookItemThumb.setImageResource(R.drawable.add_pic)
    }
    private fun TextView.badgeColor(color: Int) {
        background.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }

}