package com.example.ccl3_app.data

import com.example.ccl3_app.database.RecipeDao
import com.example.ccl3_app.database.RecipeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecipeRepository(private val recipeDao: RecipeDao) {

    //get recipes in a specific stack
    fun getRecipesByStack(stackId: Int): Flow<List<Recipe>> {
        return recipeDao.getRecipesForStack(stackId).map { entityList ->
            entityList.map { entity ->
                Recipe(
                    id = entity.id,
                    stackId = entity.stackId,
                    title = entity.title,
                    description = entity.description,
                    ingredients = entity.ingredients,
                    instructions = entity.instructions
                )
            }
        }
    }

    //get recipes without stacks
    fun getRecipesWithoutStack(): Flow<List<Recipe>> {
        return recipeDao.getRecipesWithoutStack().map { entityList ->
            entityList.map { entity ->
                Recipe(
                    id = entity.id,
                    stackId = entity.stackId,
                    title = entity.title,
                    description = entity.description,
                    ingredients = entity.ingredients,
                    instructions = entity.instructions
                )
            }
        }
    }

    fun getAllRecipes(): Flow<List<Recipe>> {
        return recipeDao.getAllRecipes().map { entityList ->
            entityList.map { entity ->
                Recipe(
                    id = entity.id,
                    stackId = entity.stackId,
                    title = entity.title,
                    description = entity.description,
                    ingredients = entity.ingredients,
                    instructions = entity.instructions
                )
            }
        }
    }

    // get recipes for a stack
    fun getRecipesForStack(stackId: Int) =
        recipeDao.getRecipesForStack(stackId).map { recipeList ->
            recipeList.map { entity ->
                Recipe(
                    id = entity.id,
                    stackId = entity.stackId,
                    title = entity.title,
                    //image = entity.image,
                    description = entity.description,
                    ingredients = entity.ingredients,
                    instructions = entity.instructions,
                )
            }
        }

    // create recipe
    suspend fun addRecipe(
        //id: Int,
        stackId: Int,
        title: String,
        //image: String,
        description: String,
        ingredients: List<String>,
        instructions: List<String>
    ) {
        recipeDao.addRecipe(
            RecipeEntity(
                stackId = stackId,
                title = title,
                description = description,
                ingredients = ingredients,
                instructions = instructions,
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
                description = recipe.description,
                ingredients = recipe.ingredients,
                instructions = recipe.instructions
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
            description = entity.description,
            ingredients = entity.ingredients,
            instructions = entity.instructions
        )
    }

    // delete recipe
    suspend fun deleteRecipe(recipe: Recipe) {
        recipeDao.deleteRecipe(
            RecipeEntity(
                id = recipe.id,
                stackId = recipe.stackId,
                title = recipe.title,
                description = recipe.description,
                ingredients = recipe.ingredients,
                instructions = recipe.instructions
            )
        )
    }

    fun searchRecipes(query: String): Flow<List<Recipe>> {
        return recipeDao.searchRecipes(query).map { entityList ->
            entityList.map { entity ->
                Recipe(
                    id = entity.id,
                    stackId = entity.stackId,
                    title = entity.title,
                    description = entity.description,
                    ingredients = entity.ingredients,
                    instructions = entity.instructions
                )
            }
        }
    }


}
