package com.example.ccl3_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ccl3_app.data.Recipe
import com.example.ccl3_app.data.RecipeRepository
import kotlinx.coroutines.launch

class RecipeFormsViewModel(
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    fun addRecipe(
        stackId: Int,
        title: String,
        //image: String,
        description: String,
        ingredients: List<String>,
        instructions: List<String>
    ) {
        viewModelScope.launch {
            recipeRepository.addRecipe(
                stackId,
                title,
                //image,
                description,
                ingredients,
                instructions
            )
        }
    }

    fun updateRecipe(recipe: Recipe) {
        viewModelScope.launch {
            recipeRepository.updateRecipe(recipe)
        }
    }
}
