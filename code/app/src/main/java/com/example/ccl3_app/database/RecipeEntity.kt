package com.example.ccl3_app.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "recipes",
    foreignKeys = [
        ForeignKey(
            entity = StackEntity::class,
            parentColumns = ["id"],
            childColumns = ["stackId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("stackId")]
)
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val stackId: Int,
    val title: String,
    val description: String = "",
    val ingredients: List<String>,
    val instructions: List<String>
)
