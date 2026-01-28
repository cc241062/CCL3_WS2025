package com.example.ccl3_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ccl3_app.data.Stack
import com.example.ccl3_app.data.StackRepository
import com.example.ccl3_app.data.RecipeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StackViewModel(
    private val stackRepository: StackRepository,
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    val stacks = stackRepository.getAllStacks().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )


    fun getRecipesForStack(stackId: Int) =
        if (stackId == ALL_RECIPES_STACK_ID) {
            recipeRepository.getAllRecipes()
        } else {
            recipeRepository.getRecipesForStack(stackId)
        }

    fun searchRecipes(query: String) =
        recipeRepository.searchRecipes(query)

    fun addStack(
        name: String = "New Stack",
        description: String = "",
        color: String = "FFB6C1",
        emoji: String = "🍳"
    ) {
        viewModelScope.launch {

            stackRepository.addStack(name,  color, emoji)
        }
    }

    fun deleteStack(stack: Stack) {
        viewModelScope.launch {
            stackRepository.deleteStack(stack)
        }
    }

    companion object {
        const val ALL_RECIPES_STACK_ID = -1
    }
}
