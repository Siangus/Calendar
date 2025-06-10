package com.example.calendar

data class MemoItem(
    val id: Int = 0,
    val date: String,
    val title: String,
    val text: String,
    val lastEditTime: String
)
