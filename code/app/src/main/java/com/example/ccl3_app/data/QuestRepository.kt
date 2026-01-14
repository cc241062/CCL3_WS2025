package com.example.ccl3_app.data

import com.example.ccl3_app.database.QuestDao
import com.example.ccl3_app.database.QuestEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class QuestRepository(
    private val questDao: QuestDao
) {

    // Get all quests as a Flow
    fun getAllQuests(): Flow<List<Quest>> =
        questDao.getAllQuests().map { questEntityList ->
            questEntityList.map { entity ->
                Quest(
                    id = entity.id,
                    title = entity.title,
                    description = entity.description,
                    isDone = entity.isDone,
                    level = entity.level
                )
            }
        }

    // Get quest by level
    suspend fun getQuestByLevel(level: Int): Quest? {
        val entity = questDao.getQuestByLevel(level)
        return entity?.let {
            Quest(
                id = it.id,
                title = it.title,
                description = it.description,
                isDone = it.isDone,
                level = it.level
            )
        }
    }

    // Complete a quest (mark as done)
    suspend fun completeQuest(quest: Quest) {
        if (quest.level == 1) {
            questDao.markQuestDone(quest.id)
            return
        }

        val previousQuest = questDao.getQuestByLevel(quest.level - 1)

        if (previousQuest?.isDone == true) {
            questDao.markQuestDone(quest.id)
        } else {
            throw IllegalStateException("Previous quest must be completed first")
        }
    }

    // Add a new quest
    suspend fun addQuest(
        title: String,
        description: String,
        level: Int
    ) {
        questDao.addQuest(
            QuestEntity(
                id = 0,
                title = title,
                description = description,
                isDone = false,
                level = level
            )
        )
    }
}