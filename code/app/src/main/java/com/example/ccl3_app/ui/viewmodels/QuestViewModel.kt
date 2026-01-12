package com.example.ccl3_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ccl3_app.data.QuestRepository
import com.example.ccl3_app.database.QuestEntity
import kotlinx.coroutines.launch

class QuestViewModel(
    private val questRepository: QuestRepository
) : ViewModel() {

    fun completeQuest(quest: QuestEntity) {
        viewModelScope.launch {
            questRepository.completeQuest(quest)
        }
    }
}
