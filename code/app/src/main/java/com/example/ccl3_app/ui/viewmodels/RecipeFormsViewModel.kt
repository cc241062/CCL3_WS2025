package com.example.ccl3_app.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ccl3_app.data.Recipe
import com.example.ccl3_app.data.RecipeRepository
import kotlinx.coroutines.launch

class RecipeFormsViewModel(
    private val repository: RecipeRepository
) : ViewModel() {

    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var ingredients by mutableStateOf("")
    var instructions by mutableStateOf("")

    private var recipeId: Int? = null

    fun loadRecipe(id: Int) {
        recipeId = id
        viewModelScope.launch {
            val recipe = repository.findRecipeById(id)
            title = recipe.title
            description = recipe.description
            ingredients = recipe.ingredients.joinToString("\n")
            instructions = recipe.instructions.joinToString("\n")
        }
    }

    fun saveRecipe(stackId: Int) {
        viewModelScope.launch {
            if (recipeId == null) {
                repository.addRecipe(
                    stackId = stackId,
                    title = title,
                    description = description,
                    ingredients = ingredients.lines(),
                    instructions = instructions.lines()
                )
            } else {
                repository.updateRecipe(
                    Recipe(
                        id = recipeId!!,
                        stackId = stackId,
                        title = title,
                        description = description,
                        ingredients = ingredients.lines(),
                        instructions = instructions.lines()
                    )
                )
            }
        }
    }
}

