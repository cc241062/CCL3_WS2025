package com.example.ccl3_app.data

import com.example.ccl3_app.db.RecipeEntity
import com.example.ccl3_app.db.RecipeDao
import kotlinx.coroutines.flow.map

class RecipeRepository(private val recipeDao: RecipeDao) {

    // get recipes for a stack
    fun getRecipesForStack(stackId: Int) =
        recipeDao.getRecipesForStack(stackId).map { recipeList ->
            recipeList.map { entity ->
                Recipe(
                    id = entity.id,
                    stackId = entity.stackId,
                    title = entity.title,
                    image = entity.image,
                    ingredients = entity.ingredients,
                    stepByStepInstructions = entity.stepByStepInstructions
                )
            }
        }

    // create recipe
    suspend fun addRecipe(
        stackId: Int,
        title: String,
        image: String,
        ingredients: List<String>,
        stepByStepInstructions: List<String>
    ) {
        recipeDao.addRecipe(
            RecipeEntity(
                id = 0,
                stackId = stackId,
                title = title,
                image = image,
                ingredients = ingredients,
                stepByStepInstructions = stepByStepInstructions
            )
        )
    }

    // update recipe
    suspend fun updateRecipe(recipe: Recipe) {
        recipeDao.updateRecipe(
            RecipeEntity(
                id = recipe.id,
                stackId = recipe.stackId,
                title = recipe.title,
                image = recipe.image,
                ingredients = recipe.ingredients,
                stepByStepInstructions = recipe.stepByStepInstructions
            )
        )
    }

    // get single recipe
    suspend fun findRecipeById(recipeId: Int): Recipe {
        val entity = recipeDao.findRecipeById(recipeId)
        return Recipe(
            id = entity.id,
            stackId = entity.stackId,
            title = entity.title,
            image = entity.image,
            ingredients = entity.ingredients,
            stepByStepInstructions = entity.stepByStepInstructions
        )
    }

    // delete recipe
    suspend fun deleteRecipe(recipe: Recipe) {
        recipeDao.deleteRecipe(
            RecipeEntity(
                id = recipe.id,
                stackId = recipe.stackId,
                title = recipe.title,
                image = recipe.image,
                ingredients = recipe.ingredients,
                stepByStepInstructions = recipe.stepByStepInstructions
            )
        )
    }
}
