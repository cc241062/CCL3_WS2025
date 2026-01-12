package com.example.ccl3_app.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.ccl3_app.database.StackDao

@Database(
    entities = [StackEntity::class, RecipeEntity::class, ProfileEntity::class],
    version = 2,
    exportSchema = false
)
abstract class OopsDatabase : RoomDatabase() {

    abstract fun StackDao(): StackDao
    abstract fun RecipeDao(): RecipeDao
    abstract fun ProfileDao(): ProfileDao


    companion object {
        @Volatile
        private var Instance: OopsDatabase? = null

        fun getDatabase(context: Context): OopsDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    OopsDatabase::class.java,
                    "oops_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
