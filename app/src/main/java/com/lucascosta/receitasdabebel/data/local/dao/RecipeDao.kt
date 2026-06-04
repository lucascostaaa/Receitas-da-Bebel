package com.lucascosta.receitasdabebel.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.lucascosta.receitasdabebel.data.local.entity.RecipeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes ORDER BY name")
    fun observeAll(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE isFavorite = 1 ORDER BY name")
    fun observeFavorites(): Flow<List<RecipeEntity>>

    @Query(
        """
        SELECT DISTINCT recipes.* FROM recipes
        LEFT JOIN recipe_ingredients ON recipe_ingredients.recipeId = recipes.id
        LEFT JOIN ingredients ON ingredients.id = recipe_ingredients.ingredientId
        WHERE recipes.name LIKE '%' || :query || '%'
            OR recipes.description LIKE '%' || :query || '%'
            OR ingredients.name LIKE '%' || :query || '%'
        ORDER BY recipes.name
        """
    )
    fun searchByName(query: String): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE id = :id")
    fun observeById(id: Long): Flow<RecipeEntity?>

    @Query("SELECT id FROM recipes WHERE LOWER(name) = LOWER(:name) LIMIT 1")
    suspend fun findIdByName(name: String): Long?

    @Query("UPDATE recipes SET isFavorite = :isFavorite, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean, updatedAt: Long)

    @Insert
    suspend fun insert(recipe: RecipeEntity): Long

    @Update
    suspend fun update(recipe: RecipeEntity)

    @Delete
    suspend fun delete(recipe: RecipeEntity)
}
