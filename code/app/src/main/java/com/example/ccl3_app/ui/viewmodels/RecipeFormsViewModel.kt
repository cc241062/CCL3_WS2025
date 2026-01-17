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
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private var recipeId: Int? = null

    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var ingredients by mutableStateOf("")
    var instructions by mutableStateOf("")
    var stackId by mutableStateOf(1)

    /*suspend*/ fun loadRecipe(id: Int) {
        recipeId = id
        viewModelScope.launch {
            val recipe = recipeRepository.findRecipeById(id)
            title = recipe.title
            description = recipe.description
            ingredients = recipe.ingredients.joinToString("\n")
            instructions = recipe.instructions.joinToString("\n")
            stackId = recipe.stackId
        }

    }

    fun saveRecipe(stackId: Int) {
        viewModelScope.launch {
            val ingredientsList = ingredients.split("\n").filter { it.isNotBlank() }
            val instructionsList = instructions.split("\n").filter { it.isNotBlank() }

            if (recipeId == null) {
                // Create new recipe
                recipeRepository.addRecipe(
                    stackId = stackId,
                    title = title,
                    description = description,
                    ingredients = ingredientsList,
                    instructions = instructionsList
                )
            } else {
                // Update existing recipe
                recipeRepository.updateRecipe(
                    Recipe(
                        id = recipeId!!,
                        stackId = stackId,
                        title = title,
                        description = description,
                        ingredients = ingredientsList,
                        instructions = instructionsList
                    )
                )
            }
        }
    }

    fun clearForm() {
        recipeId = null
        title = ""
        description = ""
        ingredients = ""
        instructions = ""
    }
}