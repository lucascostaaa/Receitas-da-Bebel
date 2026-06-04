package com.lucascosta.receitasdabebel.data.repository

import com.lucascosta.receitasdabebel.data.local.entity.CategoryEntity
import com.lucascosta.receitasdabebel.data.local.entity.IngredientEntity
import com.lucascosta.receitasdabebel.data.local.entity.PreparationStepEntity
import com.lucascosta.receitasdabebel.data.local.entity.RecipeEntity
import com.lucascosta.receitasdabebel.data.local.entity.RecipeImageEntity
import com.lucascosta.receitasdabebel.data.local.entity.RecipeIngredientEntity
import com.lucascosta.receitasdabebel.data.importer.ImportedRecipe
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    fun observeRecipes(): Flow<List<RecipeEntity>>
    fun observeFavoriteRecipes(): Flow<List<RecipeEntity>>
    fun searchRecipes(query: String): Flow<List<RecipeEntity>>
    fun observeRecipe(id: Long): Flow<RecipeEntity?>
    fun observeCategories(): Flow<List<CategoryEntity>>
    fun observeIngredients(): Flow<List<IngredientEntity>>
    fun observeRecipeIngredients(recipeId: Long): Flow<List<RecipeIngredientEntity>>
    fun observeRecipeImages(recipeId: Long): Flow<List<RecipeImageEntity>>
    fun observePreparationSteps(recipeId: Long): Flow<List<PreparationStepEntity>>

    suspend fun addRecipe(recipe: RecipeEntity): Long
    suspend fun updateRecipe(recipe: RecipeEntity)
    suspend fun deleteRecipe(recipe: RecipeEntity)
    suspend fun setRecipeFavorite(id: Long, isFavorite: Boolean)
    suspend fun addCategory(category: CategoryEntity): Long
    suspend fun addCategories(categories: List<CategoryEntity>)
    suspend fun addIngredient(ingredient: IngredientEntity): Long
    suspend fun getOrCreateIngredientId(name: String): Long
    suspend fun addRecipeIngredient(recipeIngredient: RecipeIngredientEntity): Long
    suspend fun removeRecipeIngredient(recipeIngredient: RecipeIngredientEntity)
    suspend fun addRecipeImage(image: RecipeImageEntity): Long
    suspend fun removeRecipeImage(image: RecipeImageEntity)
    suspend fun addPreparationStep(step: PreparationStepEntity): Long
    suspend fun removePreparationStep(step: PreparationStepEntity)
    suspend fun importRecipes(recipes: List<ImportedRecipe>): Int
}
