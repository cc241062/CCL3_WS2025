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

class HomeViewModel(
    private val recipeRepository: RecipeRepository,
    private val stackRepository: StackRepository
) : ViewModel() {

    // ---------- STACKS ----------
    private val _stacks = MutableStateFlow<List<Stack>>(emptyList())
    val stacks: StateFlow<List<Stack>> = _stacks.asStateFlow()

    private val _selectedStackId = MutableStateFlow<Int?>(null)
    val selectedStackId: StateFlow<Int?> = _selectedStackId.asStateFlow()

    // ---------- RECIPES ----------
    private val _featuredRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val featuredRecipes: StateFlow<List<Recipe>> = _featuredRecipes.asStateFlow()

    private val _currentRecipeIndex = MutableStateFlow(0)
    val currentRecipeIndex: StateFlow<Int> = _currentRecipeIndex.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        observeStacks()
        loadFeaturedRecipes()
    }

    // listen to DB stacks
    private fun observeStacks() {
        viewModelScope.launch {
            stackRepository.getAllStacks().collect { stackList ->
                _stacks.value = stackList

                // set default selected stack once when data arrives
                if (_selectedStackId.value == null && stackList.isNotEmpty()) {
                    _selectedStackId.value = stackList.first().id
                }
            }
        }
    }

    fun selectStack(stackId: Int) {
        _selectedStackId.value = stackId
        _currentRecipeIndex.value = 0       // start at first recipe of that stack
    }

    // all recipes from DB (we filter per stack on top)
    private fun loadFeaturedRecipes() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                recipeRepository.getAllRecipes().collect { recipes ->
                    _featuredRecipes.value = recipes
                    // keep index in range for current stack
                    val filtered = recipesForSelectedStack()
                    if (filtered.isNotEmpty() && _currentRecipeIndex.value >= filtered.size) {
                        _currentRecipeIndex.value = 0
                    }
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    // helper: recipes for the currently selected stack
    private fun recipesForSelectedStack(): List<Recipe> {
        val sid = _selectedStackId.value ?: return emptyList()
        return _featuredRecipes.value.filter { it.stackId == sid }
    }

    fun nextRecipe() {
        val recipes = recipesForSelectedStack()
        if (recipes.isNotEmpty()) {
            _currentRecipeIndex.value =
                (_currentRecipeIndex.value + 1) % recipes.size
        }
    }

    fun previousRecipe() {
        val recipes = recipesForSelectedStack()
        if (recipes.isNotEmpty()) {
            _currentRecipeIndex.value =
                if (_currentRecipeIndex.value == 0) recipes.size - 1
                else _currentRecipeIndex.value - 1
        }
    }

    fun getCurrentRecipe(): Recipe? {
        val recipes = recipesForSelectedStack()
        if (recipes.isEmpty()) return null
        val idx = _currentRecipeIndex.value.coerceIn(0, recipes.size - 1)
        return recipes[idx]
    }

    fun getRecipesForStack(stackId: Int) =
        recipeRepository.getRecipesForStack(stackId)

    fun deleteRecipe(recipe: Recipe) {
        viewModelScope.launch {
            recipeRepository.deleteRecipe(recipe)
        }
    }
}
