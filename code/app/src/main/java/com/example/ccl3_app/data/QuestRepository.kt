package com.example.ccl3_app.data

import com.example.ccl3_app.database.QuestDao
import com.example.ccl3_app.database.QuestEntity

class QuestRepository(
    private val questDao: QuestDao
) {

    suspend fun completeQuest(quest: QuestEntity) {
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
}
