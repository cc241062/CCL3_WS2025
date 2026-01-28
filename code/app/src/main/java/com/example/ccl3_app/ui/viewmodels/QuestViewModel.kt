package com.example.ccl3_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ccl3_app.data.Quest
import com.example.ccl3_app.data.QuestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuestViewModel(
    private val questRepository: QuestRepository
) : ViewModel() {

    // State for all quests
    private val _quests = MutableStateFlow<List<Quest>>(emptyList())
    val quests: StateFlow<List<Quest>> = _quests.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            questRepository.getAllQuests().collect { questList ->
                _quests.value = questList.sortedBy { it.level }
            }
        }
    }

    // Complete a quest
    fun completeQuest(quest: Quest) {
        viewModelScope.launch {
            questRepository.completeQuest(quest)
        }
    }

    // Get current quest
    fun getCurrentQuest(): Quest? {
        return _quests.value.firstOrNull { !it.isDone }
    }

    // Get completed count
    fun getCompletedCount(): Int {
        return _quests.value.count { it.isDone }
    }
}