package com.example.ccl3_app.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ccl3_app.data.Stack
import com.example.ccl3_app.data.StackRepository
import kotlinx.coroutines.launch

class StackFormsViewModel(
    private val stackRepository: StackRepository
) : ViewModel() {

    private var stackId: Int? = null

    var name by mutableStateOf("")
    var emoji by mutableStateOf("🍳")
    var color by mutableStateOf("E37434")

    suspend fun loadStack(id: Int) {
        stackId = id
        val stack = stackRepository.findStackById(id)
        if (stack != null) {
            name = stack.name
            emoji = stack.emoji
            color = stack.color
        }
    }

    fun saveStack() {
        viewModelScope.launch {
            if (stackId == null) {
                stackRepository.addStack(
                    name = name.ifBlank { "New Stack" },
                    color = color,
                    emoji = emoji
                )
            } else {
                stackRepository.updateStack(
                    Stack(
                        id = stackId!!,
                        name = name.ifBlank { "New Stack" },
                        color = color,
                        emoji = emoji
                    )
                )
            }
        }
    }

    fun clearForm() {
        stackId = null
        name = ""
        emoji = "🍳"
        color = "E37434"
    }
}
