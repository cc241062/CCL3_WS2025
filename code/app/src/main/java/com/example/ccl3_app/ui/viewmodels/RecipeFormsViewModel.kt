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

    // nullable now – we don’t assume 1 as default
    var stackId by mutableStateOf<Int?>(null)

    fun loadRecipe(id: Int) {
        recipeId = id
        viewModelScope.launch {
            val recipe = recipeRepository.findRecipeById(id)
            // if recipe is found, fill the form
            title = recipe.title
            description = recipe.description
            ingredients = recipe.ingredients.joinToString("\n")
            instructions = recipe.instructions.joinToString("\n")
            stackId = recipe.stackId
        }
    }

    /**
     * Save the recipe.
     * `stackIdParam` comes from the screen (selected stack in dropdown).
     * If it's null we just don't save to avoid crashes.
     */
    fun saveRecipe(stackIdParam: Int?) {
        viewModelScope.launch {
            val resolvedStackId = stackIdParam ?: stackId
            // if still null → nothing to do (no valid stack to link to)
            if (resolvedStackId == null) return@launch

            val ingredientsList = ingredients
                .split("\n")
                .map { it.trim() }
                .filter { it.isNotBlank() }

            val instructionsList = instructions
                .split("\n")
                .map { it.trim() }
                .filter { it.isNotBlank() }

            if (recipeId == null) {
                // Create new recipe
                recipeRepository.addRecipe(
                    stackId = resolvedStackId,
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
                        stackId = resolvedStackId,
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
        stackId = null
    }
}
