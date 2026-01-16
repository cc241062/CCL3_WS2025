package com.example.ccl3_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ccl3_app.data.RecipeRepository
import com.example.ccl3_app.data.Stack
import com.example.ccl3_app.data.StackRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.stateIn

class StackViewModel(
    private val stackRepository: StackRepository,
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    //val stacks = stackRepository.stacks

    val stacks = stackRepository.getAllStacks().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun getRecipesForStack(stackId: Int) = recipeRepository.getRecipesByStack(stackId)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun addStack(
        name: String = "New Stack",
        description: String = "",
        color: String = "FF4A90E2"
    ) {
        viewModelScope.launch {
            stackRepository.addStack(name, description, color)
        }
    }

    fun deleteStack(stack: Stack) {
        viewModelScope.launch {
            stackRepository.deleteStack(stack)
        }
    }
}
