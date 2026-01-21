package com.example.ccl3_app.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stacks")
data class StackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val color: String = "FF4A90E2",
    val emoji: String = "🍳"
)
