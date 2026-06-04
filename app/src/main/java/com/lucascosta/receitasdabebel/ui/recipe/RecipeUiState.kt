package com.lucascosta.receitasdabebel.ui.recipe

import com.lucascosta.receitasdabebel.data.local.entity.CategoryEntity
import com.lucascosta.receitasdabebel.data.local.entity.IngredientEntity
import com.lucascosta.receitasdabebel.data.local.entity.PreparationStepEntity
import com.lucascosta.receitasdabebel.data.local.entity.RecipeEntity
import com.lucascosta.receitasdabebel.data.local.entity.RecipeImageEntity
import com.lucascosta.receitasdabebel.data.local.entity.RecipeIngredientEntity

data class RecipeUiState(
    val recipes: List<RecipeEntity> = emptyList(),
    val favoriteRecipes: List<RecipeEntity> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val ingredients: List<IngredientEntity> = emptyList(),
    val searchQuery: String = "",
    val selectedRecipe: RecipeEntity? = null,
    val selectedIngredients: List<RecipeIngredientEntity> = emptyList(),
    val selectedImages: List<RecipeImageEntity> = emptyList(),
    val selectedSteps: List<PreparationStepEntity> = emptyList(),
    val form: RecipeFormState = RecipeFormState(),
    val ingredientName: String = "",
    val ingredientMeasure: String = "",
    val ingredientObservation: String = "",
    val imagePath: String = "",
    val stepDescription: String = "",
    val message: String? = null
)
