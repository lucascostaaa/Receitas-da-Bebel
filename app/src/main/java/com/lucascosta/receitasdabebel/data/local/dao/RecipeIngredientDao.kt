package com.lucascosta.receitasdabebel.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lucascosta.receitasdabebel.data.local.entity.RecipeIngredientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeIngredientDao {
    @Query("SELECT * FROM recipe_ingredients WHERE recipeId = :recipeId ORDER BY sortOrder")
    fun observeForRecipe(recipeId: Long): Flow<List<RecipeIngredientEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recipeIngredient: RecipeIngredientEntity): Long

    @Delete
    suspend fun delete(recipeIngredient: RecipeIngredientEntity)
}
