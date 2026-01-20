package com.example.ccl3_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ccl3_app.data.Recipe
import com.example.ccl3_app.data.RecipeRepository
import com.example.ccl3_app.data.Stack
import com.example.ccl3_app.data.StackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StackDetailViewModel(
    private val stackRepository: StackRepository,
    private val recipeRepository: RecipeRepository,
    private val stackId: Int
) : ViewModel() {

    companion object {
        const val ALL_RECIPES_STACK_ID = -1
    }

    private val isAllRecipes = stackId == ALL_RECIPES_STACK_ID

    private val _stack = MutableStateFlow<Stack?>(null)
    val stack: StateFlow<Stack?> = _stack.asStateFlow()

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes.asStateFlow()

    init {
        if (!isAllRecipes) loadStack()
        loadRecipes()
    }

    private fun loadStack() {
        viewModelScope.launch {
            _stack.value = stackRepository.findStackById(stackId) // Stack? (nullable)
        }
    }

    private fun loadRecipes() {
        viewModelScope.launch {
            if (isAllRecipes) {
                recipeRepository.getAllRecipes().collect { recipeList ->
                    _recipes.value = recipeList
                }
            } else {
                recipeRepository.getRecipesByStack(stackId).collect { recipeList ->
                    _recipes.value = recipeList
                }
            }
        }
    }

    fun deleteStack(stack: Stack) {
        if (isAllRecipes) return
        viewModelScope.launch {
            stackRepository.deleteStack(stack)
        }
    }
}
