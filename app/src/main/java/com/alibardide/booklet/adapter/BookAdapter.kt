package com.alibardide.booklet.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alibardide.booklet.R
import com.alibardide.booklet.data.model.Book
import java.util.*

class BookAdapter(
    private val context: Context,
    private var data: MutableList<Book>
) : RecyclerView.Adapter<BookViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.book_item, parent, false)
        data.sortWith(Comparator { p0, p1 -> p1.updatedOn.toLong().compareTo(p0.updatedOn.toLong()) })
        return BookViewHolder(context, view, this)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val item = data[position]
        val timeline = when (position) {
            0 -> -1
            data.size - 1 -> 0
            else -> 1
        }
        holder.setData(item, timeline)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun refresh(data: List<Book>) {
        this.data.clear()
        this.data.addAll(data)
        this.data.sortWith(Comparator { p0, p1 -> p1.updatedOn.toLong().compareTo(p0.updatedOn.toLong()) })
        this.notifyDataSetChanged()
    }
}