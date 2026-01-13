package com.example.ccl3_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ccl3_app.data.Recipe
import com.example.ccl3_app.data.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    // State for featured recipes carousel
    private val _featuredRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val featuredRecipes: StateFlow<List<Recipe>> = _featuredRecipes.asStateFlow()

    // Current recipe index in carousel
    private val _currentRecipeIndex = MutableStateFlow(0)
    val currentRecipeIndex: StateFlow<Int> = _currentRecipeIndex.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadFeaturedRecipes()
    }

    // Load featured recipes for the home carousel
    private fun loadFeaturedRecipes() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Get recipes for a specific stack (e.g., beginner recipes)
                // You can change stackId based on your needs
                recipeRepository.getRecipesForStack(1).collect { recipes ->
                    _featuredRecipes.value = recipes
                    if (recipes.isNotEmpty() && _currentRecipeIndex.value >= recipes.size) {
                        _currentRecipeIndex.value = 0
                    }
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Navigate to next recipe in carousel
    fun nextRecipe() {
        val recipes = _featuredRecipes.value
        if (recipes.isNotEmpty()) {
            _currentRecipeIndex.value = (_currentRecipeIndex.value + 1) % recipes.size
        }
    }

    // Navigate to previous recipe in carousel
    fun previousRecipe() {
        val recipes = _featuredRecipes.value
        if (recipes.isNotEmpty()) {
            _currentRecipeIndex.value =
                if (_currentRecipeIndex.value == 0) recipes.size - 1
                else _currentRecipeIndex.value - 1
        }
    }

    // Get current recipe being displayed
    fun getCurrentRecipe(): Recipe? {
        val recipes = _featuredRecipes.value
        return if (recipes.isNotEmpty() && _currentRecipeIndex.value < recipes.size) {
            recipes[_currentRecipeIndex.value]
        } else null
    }

    // Existing functions
    fun getRecipesForStack(stackId: Int) =
        recipeRepository.getRecipesForStack(stackId)

    fun deleteRecipe(recipe: Recipe) {
        viewModelScope.launch {
            recipeRepository.deleteRecipe(recipe)
        }
    }
}