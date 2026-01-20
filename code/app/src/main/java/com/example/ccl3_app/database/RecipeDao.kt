package com.example.ccl3_app.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.ccl3_app.data.Recipe
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRecipe(recipe: RecipeEntity)

    @Update
    suspend fun updateRecipe(recipe: RecipeEntity)

    @Delete
    suspend fun deleteRecipe(recipe: RecipeEntity)

    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun findRecipeById(id: Int): RecipeEntity

    @Query("SELECT * FROM recipes")
    fun getAllRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE stackId = :stackId")
    fun getRecipesForStack(stackId: Int): Flow<List<RecipeEntity>>

    @Query("DELETE FROM recipes WHERE stackId = :stackId")
    suspend fun deleteAllRecipesInStack(stackId: Int)

    @Query("SELECT COUNT(*) FROM recipes WHERE stackId = :stackId")
    fun getRecipeCount(stackId: Int): Flow<Int>

    @Query("SELECT * FROM recipes WHERE stackId IS NULL")
    fun getRecipesWithoutStack(): Flow<List<RecipeEntity>>

    @Query(
        """
    SELECT * FROM recipes
    WHERE title LIKE '%' || :query || '%'
    ORDER BY title ASC
    """
    )
    fun searchRecipes(query: String): Flow<List<RecipeEntity>>

}
