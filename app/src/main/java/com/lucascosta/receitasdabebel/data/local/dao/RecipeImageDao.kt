package com.lucascosta.receitasdabebel.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.lucascosta.receitasdabebel.data.local.entity.RecipeImageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeImageDao {
    @Query("SELECT * FROM recipe_images WHERE recipeId = :recipeId ORDER BY sortOrder")
    fun observeForRecipe(recipeId: Long): Flow<List<RecipeImageEntity>>

    @Insert
    suspend fun insert(image: RecipeImageEntity): Long

    @Delete
    suspend fun delete(image: RecipeImageEntity)
}
