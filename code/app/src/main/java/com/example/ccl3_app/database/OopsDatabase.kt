package com.example.ccl3_app.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [StackEntity::class, RecipeEntity::class, ProfileEntity::class, QuestEntity::class],
    version = 6,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class OopsDatabase : RoomDatabase() {

    abstract fun StackDao(): StackDao
    abstract fun RecipeDao(): RecipeDao
    abstract fun ProfileDao(): ProfileDao
    abstract fun QuestDao(): QuestDao

    companion object {
        @Volatile
        private var Instance: OopsDatabase? = null

        fun getDatabase(context: Context): OopsDatabase {
            return Instance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    OopsDatabase::class.java,
                    "oops_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                Instance = instance

                CoroutineScope(Dispatchers.IO).launch {
                    val questDao = instance.QuestDao()
                    val count = questDao.getQuestCount()
                    if (count == 0) {
                        prepopulateQuests(questDao)
                    }
                }

                instance
            }
        }

        private suspend fun prepopulateQuests(questDao: QuestDao) {
            val quests = listOf(
                QuestEntity(
                    id = 0,
                    title = "Fry an Egg",
                    description = "Learn how to fry a perfect sunny-side up egg!",
                    isDone = false,
                    level = 1
                ),
                QuestEntity(
                    id = 0,
                    title = "Green Goddess Salad",
                    description = "Blend a herb-packed green dressing and toss a crunchy salad.",
                    isDone = false,
                    level = 2
                ),
                QuestEntity(
                    id = 0,
                    title = "Beef Tartare",
                    description = "Learn the basics of preparing a classic beef tartare safely.",
                    isDone = false,
                    level = 3
                ),
                QuestEntity(
                    id = 0,
                    title = "Cut an Onion Like a Pro",
                    description = "Learn how to dice an onion safely and evenly like a professional chef.",
                    isDone = false,
                    level = 4
                ),
                QuestEntity(
                    id = 0,
                    title = "Fudge Brownies",
                    description = "Bake rich, fudgy brownies with a beautiful shiny crackly top.",
                    isDone = false,
                    level = 5
                )
            )

            quests.forEach { quest ->
                questDao.addQuest(quest)
            }
        }
    }
}
