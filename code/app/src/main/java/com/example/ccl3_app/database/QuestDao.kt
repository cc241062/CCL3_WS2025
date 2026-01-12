package com.example.ccl3_app.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addQuest(quest: QuestEntity)

    @Query("SELECT * FROM quests ORDER BY level ASC")
    fun getAllQuests(): Flow<List<QuestEntity>>

    @Query("SELECT * FROM quests WHERE level = :level")
    suspend fun getQuestByLevel(level: Int): QuestEntity?

    @Query("UPDATE quests SET isDone = 1 WHERE id = :id")
    suspend fun markQuestDone(id: Int)

    @Query("SELECT COUNT(*) FROM quests WHERE isDone = 1")
    fun getCompletedQuestCount(): Flow<Int>
}
