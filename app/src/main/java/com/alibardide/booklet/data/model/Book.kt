package com.alibardide.booklet.data.model

import java.io.Serializable

data class Book(
    val id: Long = 0,
    val name: String,
    val author: String,
    val publisher: String,
    val description: String,
    val category: String,
    val date: String,
    val pages: Int,
    val progress: Int,
    val state: Int,
    val picLocation: String
) : Serializable