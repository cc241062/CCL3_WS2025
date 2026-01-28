package com.example.ccl3_app.data

data class Quest(
    val id: Int = 0,
    val title: String,
    val description: String,
    val isDone: Boolean = false,
    val level: Int
)
