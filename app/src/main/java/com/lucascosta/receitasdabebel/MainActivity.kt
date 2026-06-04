package com.lucascosta.receitasdabebel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lucascosta.receitasdabebel.data.importer.RecipeAssetImportManager
import com.lucascosta.receitasdabebel.data.repository.LocalRecipeRepository
import com.lucascosta.receitasdabebel.ui.category.CategoriesViewModel
import com.lucascosta.receitasdabebel.ui.category.CategoriesViewModelFactory
import com.lucascosta.receitasdabebel.ui.recipe.RecipesViewModel
import com.lucascosta.receitasdabebel.ui.recipe.RecipesViewModelFactory
import com.lucascosta.receitasdabebel.ui.theme.ReceitasDaBebelTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReceitasDaBebelTheme {
                val repository = remember {
                    LocalRecipeRepository.create(applicationContext)
                }
                LaunchedEffect(repository) {
                    RecipeAssetImportManager(
                        context = applicationContext,
                        repository = repository
                    ).importFromAssets()
                }
                val categoriesViewModel: CategoriesViewModel = viewModel(
                    factory = CategoriesViewModelFactory(
                        repository = repository
                    )
                )
                val recipesViewModel: RecipesViewModel = viewModel(
                    factory = RecipesViewModelFactory(
                        repository = repository
                    )
                )

                ReceitasDaBebelApp(
                    categoriesViewModel = categoriesViewModel,
                    recipesViewModel = recipesViewModel
                )
            }
        }
    }
}
