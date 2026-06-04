package com.lucascosta.receitasdabebel.ui.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lucascosta.receitasdabebel.data.local.entity.CategoryEntity
import com.lucascosta.receitasdabebel.data.local.entity.RecipeEntity
import com.lucascosta.receitasdabebel.ui.components.EmptyState
import com.lucascosta.receitasdabebel.ui.components.MetadataPill
import com.lucascosta.receitasdabebel.ui.components.ModernCard
import com.lucascosta.receitasdabebel.ui.components.RecipeThumbnail
import com.lucascosta.receitasdabebel.ui.components.ScreenTopBar

@Composable
fun RecipeListScreen(
    uiState: RecipeUiState,
    onBack: () -> Unit,
    onCreateRecipe: () -> Unit,
    onSearchChange: (String) -> Unit,
    onOpenRecipe: (RecipeEntity) -> Unit,
    onToggleFavorite: (RecipeEntity, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategoryId by rememberSaveable { mutableStateOf<Long?>(null) }
    val categoryItems = uiState.recipes.categoryFilterItems(uiState.categories)
    val visibleRecipes = uiState.recipes.filterByCategory(selectedCategoryId)
    val selectedCategoryName = categoryItems.firstOrNull { it.id == selectedCategoryId }?.name

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ScreenTopBar(
            title = "Buscar receitas",
            subtitle = if (selectedCategoryName == null) {
                "${visibleRecipes.size} receita(s) encontrada(s)"
            } else {
                "${visibleRecipes.size} receita(s) em $selectedCategoryName"
            },
            action = {
                OutlinedButton(onClick = onBack) {
                    Text("Inicio")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 22.dp)
        )
        ModernCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchChange,
                    label = { Text("Buscar receitas") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = onCreateRecipe,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Nova receita")
                }
            }
        }
        CategoryNavigator(
            items = categoryItems,
            selectedCategoryId = selectedCategoryId,
            totalRecipes = uiState.recipes.size,
            onSelectCategory = { selectedCategoryId = it },
            modifier = Modifier.fillMaxWidth()
        )
        RecipeCards(
            recipes = visibleRecipes,
            categories = uiState.categories,
            emptyText = "Nenhuma receita encontrada.",
            onOpenRecipe = onOpenRecipe,
            onToggleFavorite = onToggleFavorite,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun FavoritesScreen(
    uiState: RecipeUiState,
    onBack: () -> Unit,
    onOpenRecipe: (RecipeEntity) -> Unit,
    onToggleFavorite: (RecipeEntity, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategoryId by rememberSaveable { mutableStateOf<Long?>(null) }
    val categoryItems = uiState.favoriteRecipes.categoryFilterItems(uiState.categories)
    val visibleRecipes = uiState.favoriteRecipes.filterByCategory(selectedCategoryId)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ScreenTopBar(
            title = "Favoritos",
            subtitle = "Receitas queridas para acessar rapido",
            action = {
                OutlinedButton(onClick = onBack) {
                    Text("Inicio")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 22.dp)
        )
        CategoryNavigator(
            items = categoryItems,
            selectedCategoryId = selectedCategoryId,
            totalRecipes = uiState.favoriteRecipes.size,
            onSelectCategory = { selectedCategoryId = it },
            modifier = Modifier.fillMaxWidth()
        )
        RecipeCards(
            recipes = visibleRecipes,
            categories = uiState.categories,
            emptyText = "Nenhuma receita favorita ainda.",
            onOpenRecipe = onOpenRecipe,
            onToggleFavorite = onToggleFavorite,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun CategoryNavigator(
    items: List<CategoryFilterItem>,
    selectedCategoryId: Long?,
    totalRecipes: Int,
    onSelectCategory: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) return

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Navegar por categoria",
            style = MaterialTheme.typography.titleMedium
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(end = 4.dp)
        ) {
            item(key = "category-filter-all") {
                CategoryFilterButton(
                    label = "Todas",
                    count = totalRecipes,
                    selected = selectedCategoryId == null,
                    onClick = { onSelectCategory(null) }
                )
            }
            items(items = items, key = { "category-filter-${it.id ?: "none"}" }) { item ->
                CategoryFilterButton(
                    label = item.name,
                    count = item.count,
                    selected = selectedCategoryId == item.id,
                    onClick = { onSelectCategory(item.id) }
                )
            }
        }
    }
}

@Composable
private fun CategoryFilterButton(
    label: String,
    count: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    val text = "$label ($count)"
    if (selected) {
        Button(onClick = onClick) {
            Text(text)
        }
    } else {
        OutlinedButton(onClick = onClick) {
            Text(text)
        }
    }
}

@Composable
fun RecipeCards(
    recipes: List<RecipeEntity>,
    categories: List<CategoryEntity> = emptyList(),
    emptyText: String,
    onOpenRecipe: (RecipeEntity) -> Unit,
    onToggleFavorite: (RecipeEntity, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    if (recipes.isEmpty()) {
        EmptyState(
            title = emptyText,
            description = "Cadastre receitas ou ajuste sua busca.",
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )
        return
    }

    val groupedRecipes = recipes.groupByCategory(categories)

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        groupedRecipes.forEach { group ->
            item(key = "category-${group.categoryId ?: "none"}") {
                CategorySectionHeader(group = group)
            }
            items(items = group.recipes, key = RecipeEntity::id) { recipe ->
                RecipeCard(
                    recipe = recipe,
                    onOpenRecipe = onOpenRecipe,
                    onToggleFavorite = onToggleFavorite
                )
            }
        }
    }
}

@Composable
private fun CategorySectionHeader(group: RecipeCategoryGroup) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = group.name,
            style = MaterialTheme.typography.titleMedium
        )
        MetadataPill(
            text = "${group.recipes.size} receita(s)",
            modifier = Modifier.padding(vertical = 2.dp)
        )
    }
}

@Composable
private fun RecipeCard(
    recipe: RecipeEntity,
    onOpenRecipe: (RecipeEntity) -> Unit,
    onToggleFavorite: (RecipeEntity, Boolean) -> Unit
) {
    ModernCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RecipeThumbnail(text = recipe.name)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = recipe.name, style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (recipe.preparationTimeMinutes > 0) {
                        MetadataPill(
                            text = "${recipe.preparationTimeMinutes} min",
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                    MetadataPill(
                        text = if (recipe.isFavorite) "Favorita" else "Receita",
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
                Row {
                    Button(onClick = { onOpenRecipe(recipe) }) {
                        Text("Abrir")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = { onToggleFavorite(recipe, !recipe.isFavorite) }
                    ) {
                        Text(if (recipe.isFavorite) "Remover" else "Favoritar")
                    }
                }
            }
        }
    }
}

private data class RecipeCategoryGroup(
    val categoryId: Long?,
    val name: String,
    val recipes: List<RecipeEntity>
)

private data class CategoryFilterItem(
    val id: Long?,
    val name: String,
    val count: Int
)

private fun List<RecipeEntity>.filterByCategory(categoryId: Long?): List<RecipeEntity> {
    return categoryId?.let { selectedId ->
        filter { it.categoryId == selectedId }
    } ?: this
}

private fun List<RecipeEntity>.categoryFilterItems(
    categories: List<CategoryEntity>
): List<CategoryFilterItem> {
    val categoryById = categories.associateBy(CategoryEntity::id)
    val categoryOrder = categories.mapIndexed { index, category -> category.id to index }.toMap()

    return groupBy { it.categoryId }
        .map { (categoryId, recipes) ->
            CategoryFilterItem(
                id = categoryId,
                name = categoryById[categoryId]?.name ?: "Sem categoria",
                count = recipes.size
            )
        }
        .sortedWith(
            compareBy<CategoryFilterItem> {
                categoryOrder[it.id] ?: Int.MAX_VALUE
            }.thenBy { it.name.lowercase() }
        )
}

private fun List<RecipeEntity>.groupByCategory(
    categories: List<CategoryEntity>
): List<RecipeCategoryGroup> {
    val categoryById = categories.associateBy(CategoryEntity::id)
    val categoryOrder = categories.mapIndexed { index, category -> category.id to index }.toMap()

    return groupBy { it.categoryId }
        .map { (categoryId, recipes) ->
            RecipeCategoryGroup(
                categoryId = categoryId,
                name = categoryById[categoryId]?.name ?: "Sem categoria",
                recipes = recipes.sortedBy { it.name.lowercase() }
            )
        }
        .sortedWith(
            compareBy<RecipeCategoryGroup> {
                categoryOrder[it.categoryId] ?: Int.MAX_VALUE
            }.thenBy { it.name.lowercase() }
        )
}

@Composable
fun ScreenHeader(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(top = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedButton(onClick = onBack) {
            Text("Voltar")
        }
        Text(text = title, style = MaterialTheme.typography.headlineMedium)
    }
}
