package com.lucascosta.receitasdabebel

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lucascosta.receitasdabebel.data.local.entity.RecipeEntity
import com.lucascosta.receitasdabebel.navigation.AppScreen
import com.lucascosta.receitasdabebel.share.shareTextToWhatsApp
import com.lucascosta.receitasdabebel.ui.category.CategoriesScreen
import com.lucascosta.receitasdabebel.ui.category.CategoriesViewModel
import com.lucascosta.receitasdabebel.ui.home.HomeScreen
import com.lucascosta.receitasdabebel.ui.onboarding.OnboardingScreen
import com.lucascosta.receitasdabebel.ui.recipe.FavoritesScreen
import com.lucascosta.receitasdabebel.ui.recipe.RecipeDetailScreen
import com.lucascosta.receitasdabebel.ui.recipe.RecipeFormScreen
import com.lucascosta.receitasdabebel.ui.recipe.RecipeListScreen
import com.lucascosta.receitasdabebel.ui.recipe.RecipesViewModel

@Composable
fun ReceitasDaBebelApp(
    categoriesViewModel: CategoriesViewModel,
    recipesViewModel: RecipesViewModel
) {
    val context = LocalContext.current
    val preferences = context.getSharedPreferences("receitas_da_bebel", android.content.Context.MODE_PRIVATE)
    val hasSeenOnboarding = preferences.getBoolean("has_seen_onboarding", false)
    var currentScreen: AppScreen by rememberSaveable {
        mutableStateOf(if (hasSeenOnboarding) AppScreen.Home else AppScreen.Onboarding)
    }
    var isEditingRecipeDetail by rememberSaveable { mutableStateOf(false) }

    fun openRecipe(recipe: RecipeEntity) {
        recipesViewModel.selectRecipe(recipe.id)
        isEditingRecipeDetail = false
        currentScreen = AppScreen.RecipeDetail
    }

    Scaffold(
        bottomBar = {
            if (currentScreen in bottomNavigationScreens) {
                AppBottomBar(
                    currentScreen = currentScreen,
                    onNavigate = { screen ->
                        isEditingRecipeDetail = false
                        currentScreen = screen
                    }
                )
            }
        },
        floatingActionButton = {
            if (currentScreen in bottomNavigationScreens) {
                FloatingActionButton(
                    onClick = {
                        recipesViewModel.startNewRecipe()
                        currentScreen = AppScreen.RecipeForm
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Text("+")
                }
            }
        }
    ) { innerPadding ->
        when (currentScreen) {
            AppScreen.Onboarding -> OnboardingScreen(
                onEnter = {
                    preferences.edit().putBoolean("has_seen_onboarding", true).apply()
                    currentScreen = AppScreen.Home
                },
                modifier = Modifier.padding(innerPadding)
            )

            AppScreen.Home -> HomeScreen(
                onOpenCategories = { currentScreen = AppScreen.Categories },
                onOpenRecipes = { currentScreen = AppScreen.Recipes },
                onCreateRecipe = {
                    recipesViewModel.startNewRecipe()
                    currentScreen = AppScreen.RecipeForm
                },
                onOpenFavorites = { currentScreen = AppScreen.Favorites },
                modifier = Modifier.padding(innerPadding)
            )

            AppScreen.Categories -> CategoriesScreen(
                viewModel = categoriesViewModel,
                onBack = { currentScreen = AppScreen.Home },
                modifier = Modifier.padding(innerPadding)
            )

            AppScreen.Recipes -> RecipeListScreen(
                uiState = recipesViewModel.uiState.collectAsStateWithLifecycle().value,
                onBack = { currentScreen = AppScreen.Home },
                onCreateRecipe = {
                    recipesViewModel.startNewRecipe()
                    currentScreen = AppScreen.RecipeForm
                },
                onSearchChange = recipesViewModel::updateSearchQuery,
                onOpenRecipe = ::openRecipe,
                onToggleFavorite = recipesViewModel::setFavorite,
                modifier = Modifier.padding(innerPadding)
            )

            AppScreen.RecipeForm -> RecipeFormScreen(
                uiState = recipesViewModel.uiState.collectAsStateWithLifecycle().value,
                onBack = { currentScreen = AppScreen.Recipes },
                onFormChange = recipesViewModel::updateForm,
                onSave = {
                    recipesViewModel.saveRecipe {
                        isEditingRecipeDetail = false
                        currentScreen = AppScreen.RecipeDetail
                    }
                },
                modifier = Modifier.padding(innerPadding)
            )

            AppScreen.RecipeDetail -> RecipeDetailScreen(
                uiState = recipesViewModel.uiState.collectAsStateWithLifecycle().value,
                onBack = { currentScreen = AppScreen.Recipes },
                isEditing = isEditingRecipeDetail,
                onEdit = { recipe ->
                    recipesViewModel.startEditRecipe(recipe)
                    isEditingRecipeDetail = true
                },
                onCancelEdit = { isEditingRecipeDetail = false },
                onFormChange = recipesViewModel::updateForm,
                onSaveEdit = {
                    recipesViewModel.saveRecipe {
                        isEditingRecipeDetail = false
                    }
                },
                onDelete = { recipe ->
                    recipesViewModel.deleteRecipe(recipe) {
                        isEditingRecipeDetail = false
                        currentScreen = AppScreen.Recipes
                    }
                },
                onToggleFavorite = recipesViewModel::setFavorite,
                onShareRecipe = { text ->
                    shareTextToWhatsApp(
                        context = context,
                        text = text,
                        title = "Compartilhar receita"
                    )
                },
                onIngredientNameChange = recipesViewModel::updateIngredientName,
                onIngredientMeasureChange = recipesViewModel::updateIngredientMeasure,
                onIngredientObservationChange = recipesViewModel::updateIngredientObservation,
                onAddIngredient = recipesViewModel::addIngredientToSelectedRecipe,
                onImagePathChange = recipesViewModel::updateImagePath,
                onAddImage = recipesViewModel::addImageToSelectedRecipe,
                onStepDescriptionChange = recipesViewModel::updateStepDescription,
                onAddStep = recipesViewModel::addPreparationStepToSelectedRecipe,
                modifier = Modifier.padding(innerPadding)
            )

            AppScreen.Favorites -> FavoritesScreen(
                uiState = recipesViewModel.uiState.collectAsStateWithLifecycle().value,
                onBack = { currentScreen = AppScreen.Home },
                onOpenRecipe = ::openRecipe,
                onToggleFavorite = recipesViewModel::setFavorite,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

private val bottomNavigationScreens = setOf(
    AppScreen.Home,
    AppScreen.Recipes,
    AppScreen.Categories,
    AppScreen.Favorites
)

@Composable
private fun AppBottomBar(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        AppBottomItem(
            label = "Inicio",
            selected = currentScreen == AppScreen.Home,
            onClick = { onNavigate(AppScreen.Home) }
        )
        AppBottomItem(
            label = "Receitas",
            selected = currentScreen == AppScreen.Recipes,
            onClick = { onNavigate(AppScreen.Recipes) }
        )
        AppBottomItem(
            label = "Categorias",
            selected = currentScreen == AppScreen.Categories,
            onClick = { onNavigate(AppScreen.Categories) }
        )
        AppBottomItem(
            label = "Favoritos",
            selected = currentScreen == AppScreen.Favorites,
            onClick = { onNavigate(AppScreen.Favorites) }
        )
    }
}

@Composable
private fun RowScope.AppBottomItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.weight(1f)
    ) {
        Text(
            text = label,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}
