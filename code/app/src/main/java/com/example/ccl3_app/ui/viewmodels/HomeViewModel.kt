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
import kotlinx.coroutines.flow.combine
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
        println("HomeViewModel: Selecting stack $stackId")
        _selectedStackId.value = stackId
        _currentRecipeIndex.value = 0       // start at first recipe of that stack
    }

    // all recipes from DB (we filter per stack on top)
    private fun loadFeaturedRecipes() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                recipeRepository.getAllRecipes().collect { recipes ->
                    println("HomeViewModel: Loaded ${recipes.size} recipes")
                    _featuredRecipes.value = recipes
                    // keep index in range for current stack
                    val filtered = recipesForSelectedStack()
                    println("HomeViewModel: ${filtered.size} recipes in selected stack")
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
        println("HomeViewModel: nextRecipe called, current index: ${_currentRecipeIndex.value}, total recipes: ${recipes.size}")
        if (recipes.isNotEmpty()) {
            val newIndex = (_currentRecipeIndex.value + 1) % recipes.size
            println("HomeViewModel: Moving to index $newIndex")
            _currentRecipeIndex.value = newIndex
        } else {
            println("HomeViewModel: No recipes available")
        }
    }

    fun previousRecipe() {
        val recipes = recipesForSelectedStack()
        println("HomeViewModel: previousRecipe called, current index: ${_currentRecipeIndex.value}, total recipes: ${recipes.size}")
        if (recipes.isNotEmpty()) {
            val newIndex = if (_currentRecipeIndex.value == 0) recipes.size - 1
            else _currentRecipeIndex.value - 1
            println("HomeViewModel: Moving to index $newIndex")
            _currentRecipeIndex.value = newIndex
        } else {
            println("HomeViewModel: No recipes available")
        }
    }

    fun getCurrentRecipe(): Recipe? {
        val recipes = recipesForSelectedStack()
        if (recipes.isEmpty()) {
            println("HomeViewModel: getCurrentRecipe - no recipes")
            return null
        }
        val idx = _currentRecipeIndex.value.coerceIn(0, recipes.size - 1)
        val recipe = recipes[idx]
        println("HomeViewModel: getCurrentRecipe - returning recipe at index $idx: ${recipe.title}")
        return recipe
    }

    fun getRecipesForStack(stackId: Int) =
        recipeRepository.getRecipesForStack(stackId)

    fun deleteRecipe(recipe: Recipe) {
        viewModelScope.launch {
            recipeRepository.deleteRecipe(recipe)
        }
    }
}