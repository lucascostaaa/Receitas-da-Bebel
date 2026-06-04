package com.lucascosta.receitasdabebel.data.importer

import android.content.Context
import android.util.Log
import com.lucascosta.receitasdabebel.data.repository.RecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecipeAssetImportManager(
    private val context: Context,
    private val repository: RecipeRepository
) {
    suspend fun importFromAssets(fileName: String = ASSET_FILE_NAME): Int =
        withContext(Dispatchers.IO) {
            runCatching {
                if (!context.assets.list("")?.contains(fileName).orFalse()) {
                    return@withContext 0
                }

                val rawJson = context.assets.open(fileName).bufferedReader().use { it.readText() }
                val recipes = RecipeImportParser.parse(rawJson)
                repository.importRecipes(recipes)
            }.getOrElse { error ->
                Log.e(TAG, "Nao foi possivel importar receitas dos assets.", error)
                0
            }
        }

    private fun Boolean?.orFalse(): Boolean = this ?: false

    companion object {
        const val ASSET_FILE_NAME = "recipes_import.json"
        private const val TAG = "RecipeAssetImport"
    }
}
