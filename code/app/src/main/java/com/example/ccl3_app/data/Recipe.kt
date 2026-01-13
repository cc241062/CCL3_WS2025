package com.example.ccl3_app.data

data class Recipe(
    val id: Int = 0,
    val stackId: Int,
    val title: String,
    //val image: String,
    val description: String,
    val ingredients: List<String>,
    val instructions: List<String>
)

