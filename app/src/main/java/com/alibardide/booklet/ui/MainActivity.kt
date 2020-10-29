package com.alibardide.booklet.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibardide.booklet.R
import com.alibardide.booklet.adapter.BookAdapter
import com.alibardide.booklet.data.local.db.AppDatabase
import com.alibardide.booklet.data.local.db.dao.BookDao
import com.alibardide.booklet.data.model.Book
import com.alibardide.booklet.data.repository.AppRepository
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: BookAdapter
    private lateinit var repository: AppRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val database = AppDatabase(this)
        val bookDao = BookDao(database)
        repository = AppRepository(bookDao)
        adapter = BookAdapter(this, repository.findAllBooks().toMutableList())

        mainTimeline.layoutManager = LinearLayoutManager(this)
        mainTimeline.adapter = adapter
        mainTimeline.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 10)
                    mainNewEventButton.hide()
                else if (dy < -5)
                    mainNewEventButton.show()
            }
        })

        mainNewEventButton.setOnClickListener {
            val intent = Intent(this, BookActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.refresh(repository.findAllBooks())
    }
}