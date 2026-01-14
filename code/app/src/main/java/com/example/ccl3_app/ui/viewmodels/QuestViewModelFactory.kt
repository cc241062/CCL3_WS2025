package com.example.ccl3_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ccl3_app.data.QuestRepository

class QuestViewModelFactory(
    private val questRepository: QuestRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuestViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuestViewModel(questRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}