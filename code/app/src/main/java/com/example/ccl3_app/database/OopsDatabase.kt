package com.example.ccl3_app.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.ccl3_app.database.StackDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [StackEntity::class, RecipeEntity::class, ProfileEntity::class, QuestEntity::class],
    version = 3,
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
                Room.databaseBuilder(
                    context,
                    OopsDatabase::class.java,
                    "oops_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Prepopulate the database with quests
                            Instance?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    prepopulateQuests(database.QuestDao())
                                }
                            }
                        }
                    })
                    .build()
                    .also { Instance = it }
            }
        }

        private suspend fun prepopulateQuests(questDao: QuestDao) {
            val quests = listOf(
                QuestEntity(0, "Boil Water", "Learn the basics of boiling water safely", false, 1),
                QuestEntity(0, "Make Toast", "Toast bread to golden perfection", false, 2),
                QuestEntity(0, "Scramble Eggs", "Master the art of fluffy scrambled eggs", false, 3),
                QuestEntity(0, "Fry an Egg", "Learn how to fry a perfect sunny-side up egg!", false, 4),
                QuestEntity(0, "Make Tea", "Brew a perfect cup of tea", false, 5),
                QuestEntity(0, "Cook Pasta", "Boil pasta al dente", false, 6),
                QuestEntity(0, "Make a Sandwich", "Create a delicious sandwich", false, 7),
                QuestEntity(0, "Dice Vegetables", "Learn proper knife skills for dicing", false, 8),
                QuestEntity(0, "Make a Salad", "Prepare a fresh and healthy salad", false, 9),
                QuestEntity(0, "Bake Cookies", "Bake your first batch of cookies", false, 10)
            )

            quests.forEach { quest ->
                questDao.addQuest(quest)
            }
        }
    }
}