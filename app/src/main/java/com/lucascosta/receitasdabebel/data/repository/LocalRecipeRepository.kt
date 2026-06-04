package com.lucascosta.receitasdabebel.data.repository

import android.content.Context
import androidx.room.withTransaction
import com.lucascosta.receitasdabebel.data.importer.ImportedRecipe
import com.lucascosta.receitasdabebel.data.local.database.AppDatabase
import com.lucascosta.receitasdabebel.data.local.entity.CategoryEntity
import com.lucascosta.receitasdabebel.data.local.entity.IngredientEntity
import com.lucascosta.receitasdabebel.data.local.entity.PreparationStepEntity
import com.lucascosta.receitasdabebel.data.local.entity.RecipeEntity
import com.lucascosta.receitasdabebel.data.local.entity.RecipeImageEntity
import com.lucascosta.receitasdabebel.data.local.entity.RecipeIngredientEntity
import kotlinx.coroutines.flow.Flow

class LocalRecipeRepository(
    private val database: AppDatabase
) : RecipeRepository {
    private val categoryDao = database.categoryDao()
    private val ingredientDao = database.ingredientDao()
    private val recipeDao = database.recipeDao()
    private val recipeImageDao = database.recipeImageDao()
    private val recipeIngredientDao = database.recipeIngredientDao()
    private val preparationStepDao = database.preparationStepDao()

    override fun observeRecipes(): Flow<List<RecipeEntity>> = recipeDao.observeAll()

    override fun observeFavoriteRecipes(): Flow<List<RecipeEntity>> = recipeDao.observeFavorites()

    override fun searchRecipes(query: String): Flow<List<RecipeEntity>> =
        recipeDao.searchByName(query.trim())

    override fun observeRecipe(id: Long): Flow<RecipeEntity?> = recipeDao.observeById(id)

    override fun observeCategories(): Flow<List<CategoryEntity>> = categoryDao.observeAll()

    override fun observeIngredients(): Flow<List<IngredientEntity>> = ingredientDao.observeAll()

    override fun observeRecipeIngredients(recipeId: Long): Flow<List<RecipeIngredientEntity>> =
        recipeIngredientDao.observeForRecipe(recipeId)

    override fun observeRecipeImages(recipeId: Long): Flow<List<RecipeImageEntity>> =
        recipeImageDao.observeForRecipe(recipeId)

    override fun observePreparationSteps(recipeId: Long): Flow<List<PreparationStepEntity>> =
        preparationStepDao.observeForRecipe(recipeId)

    override suspend fun addRecipe(recipe: RecipeEntity): Long = recipeDao.insert(recipe)

    override suspend fun updateRecipe(recipe: RecipeEntity) {
        recipeDao.update(recipe.copy(updatedAt = System.currentTimeMillis()))
    }

    override suspend fun deleteRecipe(recipe: RecipeEntity) {
        recipeDao.delete(recipe)
    }

    override suspend fun setRecipeFavorite(id: Long, isFavorite: Boolean) {
        recipeDao.updateFavorite(
            id = id,
            isFavorite = isFavorite,
            updatedAt = System.currentTimeMillis()
        )
    }

    override suspend fun addCategory(category: CategoryEntity): Long =
        if (categoryDao.existsByName(category.name)) {
            INSERT_IGNORED
        } else {
            categoryDao.insert(category)
        }

    override suspend fun addCategories(categories: List<CategoryEntity>) {
        categoryDao.insertAll(categories)
    }

    override suspend fun addIngredient(ingredient: IngredientEntity): Long =
        ingredientDao.insert(ingredient)

    override suspend fun getOrCreateIngredientId(name: String): Long {
        val normalizedName = name.trim()
        ingredientDao.findByName(normalizedName)?.let { return it.id }

        val insertedId = ingredientDao.insert(IngredientEntity(name = normalizedName))
        if (insertedId != INSERT_IGNORED) {
            return insertedId
        }

        return ingredientDao.findByName(normalizedName)?.id ?: INSERT_IGNORED
    }

    override suspend fun addRecipeIngredient(recipeIngredient: RecipeIngredientEntity): Long =
        recipeIngredientDao.insert(recipeIngredient)

    override suspend fun removeRecipeIngredient(recipeIngredient: RecipeIngredientEntity) {
        recipeIngredientDao.delete(recipeIngredient)
    }

    override suspend fun addRecipeImage(image: RecipeImageEntity): Long =
        recipeImageDao.insert(image)

    override suspend fun removeRecipeImage(image: RecipeImageEntity) {
        recipeImageDao.delete(image)
    }

    override suspend fun addPreparationStep(step: PreparationStepEntity): Long =
        preparationStepDao.insert(step)

    override suspend fun removePreparationStep(step: PreparationStepEntity) {
        preparationStepDao.delete(step)
    }

    override suspend fun importRecipes(recipes: List<ImportedRecipe>): Int {
        if (recipes.isEmpty()) return 0

        var importedCount = 0
        database.withTransaction {
            recipes.forEach { importedRecipe ->
                val recipeName = importedRecipe.name.trim()
                if (recipeName.isBlank() || recipeDao.findIdByName(recipeName) != null) {
                    return@forEach
                }

                val categoryId = importedRecipe.category
                    .trim()
                    .takeIf { it.isNotBlank() }
                    ?.let { categoryName -> getOrCreateCategoryId(categoryName) }

                val recipeId = recipeDao.insert(
                    RecipeEntity(
                        name = recipeName,
                        categoryId = categoryId,
                        description = importedRecipe.description.trim(),
                        preparationMode = importedRecipe.preparationMode.trim(),
                        preparationTimeMinutes = importedRecipe.preparationTimeMinutes,
                        notes = importedRecipe.notes.trim()
                    )
                )

                importedRecipe.ingredients.forEachIndexed { index, ingredient ->
                    val ingredientName = ingredient.name.trim()
                    if (ingredientName.isBlank()) return@forEachIndexed

                    val ingredientId = getOrCreateIngredientId(ingredientName)
                    if (ingredientId <= 0) return@forEachIndexed

                    recipeIngredientDao.insert(
                        RecipeIngredientEntity(
                            recipeId = recipeId,
                            ingredientId = ingredientId,
                            originalText = ingredient.measure.trim(),
                            observation = ingredient.observation.trim(),
                            sortOrder = index
                        )
                    )
                }

                importedRecipe.steps.forEachIndexed { index, step ->
                    preparationStepDao.insert(
                        PreparationStepEntity(
                            recipeId = recipeId,
                            stepNumber = index + 1,
                            description = step.trim()
                        )
                    )
                }

                importedRecipe.images.forEachIndexed { index, imagePath ->
                    recipeImageDao.insert(
                        RecipeImageEntity(
                            recipeId = recipeId,
                            imagePath = imagePath.trim(),
                            sortOrder = index
                        )
                    )
                }

                importedCount++
            }
        }

        return importedCount
    }

    private suspend fun getOrCreateCategoryId(name: String): Long {
        categoryDao.findByName(name)?.let { return it.id }

        val insertedId = categoryDao.insert(CategoryEntity(name = name))
        if (insertedId != INSERT_IGNORED) {
            return insertedId
        }

        return categoryDao.findByName(name)?.id ?: INSERT_IGNORED
    }

    companion object {
        private const val INSERT_IGNORED = -1L

        fun create(context: Context): RecipeRepository =
            LocalRecipeRepository(AppDatabase.getInstance(context))
    }
}
