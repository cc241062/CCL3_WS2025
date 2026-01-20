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
    var description by mutableStateOf("")
    var emoji by mutableStateOf("🍳")
    var color by mutableStateOf("FFB6C1")

    suspend fun loadStack(id: Int) {
        stackId = id

        val stack: Stack? = stackRepository.findStackById(id)

        // If the stack doesn't exist (or it's the virtual All Recipes id),
        // just keep defaults and don't crash.
        if (stack != null) {
            name = stack.name
            description = stack.description
            emoji = "🍳" // still static for now
            color = stack.color
        } else {
            // Optional: reset form or keep as-is
            // clearForm()
        }
    }

    fun saveStack() {
        viewModelScope.launch {
            if (stackId == null) {
                // Create new stack
                stackRepository.addStack(
                    name = name.ifBlank { "New Stack" },
                    description = description,
                    color = color
                )
            } else {
                // Update existing stack
                stackRepository.updateStack(
                    Stack(
                        id = stackId!!,
                        name = name.ifBlank { "New Stack" },
                        description = description,
                        color = color
                    )
                )
            }
        }
    }

    fun clearForm() {
        stackId = null
        name = ""
        description = ""
        emoji = "🍳"
        color = "FFB6C1"
    }
}
