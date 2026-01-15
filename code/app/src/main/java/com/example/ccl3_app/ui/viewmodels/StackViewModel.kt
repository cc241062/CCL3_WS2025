package com.example.ccl3_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ccl3_app.data.Stack
import com.example.ccl3_app.data.StackRepository
import kotlinx.coroutines.launch

class StackViewModel(
    private val repository: StackRepository
) : ViewModel() {

    val stacks = repository.stacks

    fun addStack(
        name: String = "New Stack",
        description: String = "",
        color: String = "FF4A90E2"
    ) {
        viewModelScope.launch {
            repository.addStack(name, description, color)
        }
    }

    fun deleteStack(stack: Stack) {
        viewModelScope.launch {
            repository.deleteStack(stack)
        }
    }
}
