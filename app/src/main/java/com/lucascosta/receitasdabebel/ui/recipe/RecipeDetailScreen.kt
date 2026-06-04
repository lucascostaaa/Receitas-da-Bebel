package com.lucascosta.receitasdabebel.ui.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lucascosta.receitasdabebel.data.local.entity.RecipeEntity
import com.lucascosta.receitasdabebel.ui.components.MetadataPill
import com.lucascosta.receitasdabebel.ui.components.ModernCard
import com.lucascosta.receitasdabebel.ui.components.RecipeThumbnail
import com.lucascosta.receitasdabebel.ui.components.ResponsiveContent
import com.lucascosta.receitasdabebel.ui.components.ScreenTopBar

@Composable
fun RecipeDetailScreen(
    uiState: RecipeUiState,
    onBack: () -> Unit,
    isEditing: Boolean,
    onEdit: (RecipeEntity) -> Unit,
    onCancelEdit: () -> Unit,
    onFormChange: (RecipeFormState) -> Unit,
    onSaveEdit: () -> Unit,
    onDelete: (RecipeEntity) -> Unit,
    onToggleFavorite: (RecipeEntity, Boolean) -> Unit,
    onShareRecipe: (String) -> Unit,
    onIngredientNameChange: (String) -> Unit,
    onIngredientMeasureChange: (String) -> Unit,
    onIngredientObservationChange: (String) -> Unit,
    onAddIngredient: () -> Unit,
    onImagePathChange: (String) -> Unit,
    onAddImage: () -> Unit,
    onStepDescriptionChange: (String) -> Unit,
    onAddStep: () -> Unit,
    modifier: Modifier = Modifier
) {
    val recipe = uiState.selectedRecipe
    val ingredientNamesById = remember(uiState.ingredients) {
        uiState.ingredients.associate { ingredient ->
            ingredient.id to ingredient.name
        }
    }

    ResponsiveContent(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ScreenTopBar(
                title = "Detalhes",
                subtitle = "Receita preservada no caderno digital",
                action = {
                    OutlinedButton(onClick = onBack) {
                        Text("Voltar")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 22.dp)
            )

            if (recipe == null) {
                DetailBlock(
                    title = "Nenhuma receita selecionada",
                    text = "Volte para a listagem e escolha uma receita."
                )
                return@Column
            }

            RecipeHeroCard(
                recipe = recipe,
                isEditing = isEditing,
                onEdit = onEdit,
                onCancelEdit = onCancelEdit,
                onToggleFavorite = onToggleFavorite,
                onDelete = onDelete,
                onShareRecipe = {
                    onShareRecipe(
                        formatRecipeForSharing(
                            recipe = recipe,
                            ingredients = uiState.selectedIngredients,
                            steps = uiState.selectedSteps,
                            ingredientNamesById = ingredientNamesById
                        )
                    )
                }
            )

            if (isEditing) {
                EditRecipeBlock(
                    uiState = uiState,
                    onFormChange = onFormChange,
                    onSaveEdit = onSaveEdit
                )
            } else {
                DetailBlock(
                    title = "Descricao",
                    text = recipe.description.ifBlank { "Sem descricao." }
                )
                DetailBlock(
                    title = "Modo de preparo",
                    text = recipe.preparationMode.ifBlank { "Nao informado." }
                )
                DetailBlock(
                    title = "Informacoes",
                    text = buildString {
                        if (recipe.preparationTimeMinutes > 0) append("Tempo: ${recipe.preparationTimeMinutes} min")
                    }.ifBlank { "Sem informacoes adicionais." }
                )
                DetailBlock(
                    title = "Observacoes",
                    text = recipe.notes.ifBlank { "Sem observacoes." }
                )
            }

            Text(text = "Ingredientes", style = MaterialTheme.typography.titleLarge)
            if (uiState.selectedIngredients.isEmpty()) {
                DetailBlock(
                    title = "Nenhum ingrediente",
                    text = "Adicione ingredientes para estruturar a receita."
                )
            } else {
                uiState.selectedIngredients.forEach { recipeIngredient ->
                    val ingredientName = ingredientNamesById[recipeIngredient.ingredientId] ?: "Ingrediente"
                    DetailBlock(
                        title = ingredientName,
                        text = formatIngredientText(recipeIngredient)
                    )
                }
            }

            ModernCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = "Adicionar ingrediente", style = MaterialTheme.typography.titleLarge)
                    AddIngredientFields(
                        uiState = uiState,
                        onIngredientNameChange = onIngredientNameChange,
                        onIngredientMeasureChange = onIngredientMeasureChange,
                        onIngredientObservationChange = onIngredientObservationChange,
                        onAddIngredient = onAddIngredient
                    )
                }
            }

            Text(text = "Modo de preparo passo a passo", style = MaterialTheme.typography.titleLarge)
            if (uiState.selectedSteps.isEmpty()) {
                DetailBlock(
                    title = "Nenhum passo",
                    text = "Adicione os passos para facilitar o preparo."
                )
            } else {
                uiState.selectedSteps.forEach { step ->
                    DetailBlock(
                        title = "${step.stepNumber}. Passo",
                        text = step.description
                    )
                }
            }

            ModernCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = "Adicionar passo", style = MaterialTheme.typography.titleLarge)
                    OutlinedTextField(
                        value = uiState.stepDescription,
                        onValueChange = onStepDescriptionChange,
                        label = { Text("Descricao do passo") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(onClick = onAddStep, modifier = Modifier.fillMaxWidth()) {
                        Text("Adicionar passo")
                    }
                }
            }

            Text(text = "Imagens do caderno", style = MaterialTheme.typography.titleLarge)
            if (uiState.selectedImages.isEmpty()) {
                DetailBlock(
                    title = "Nenhuma imagem",
                    text = "Anexe uma foto da pagina original do caderno."
                )
            } else {
                uiState.selectedImages.forEach { image ->
                    DetailBlock(title = "Imagem ${image.sortOrder + 1}", text = image.imagePath)
                }
            }

            ModernCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 120.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.imagePath,
                        onValueChange = onImagePathChange,
                        label = { Text("Caminho da imagem") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = onAddImage,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Anexar imagem")
                    }
                    uiState.message?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
private fun RecipeHeroCard(
    recipe: RecipeEntity,
    isEditing: Boolean,
    onEdit: (RecipeEntity) -> Unit,
    onCancelEdit: () -> Unit,
    onToggleFavorite: (RecipeEntity, Boolean) -> Unit,
    onDelete: (RecipeEntity) -> Unit,
    onShareRecipe: () -> Unit
) {
    ModernCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                RecipeThumbnail(text = recipe.name)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = recipe.name, style = MaterialTheme.typography.headlineMedium)
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
                }
            }
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                if (maxWidth < 320.dp) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (isEditing) {
                            OutlinedButton(onClick = onCancelEdit, modifier = Modifier.fillMaxWidth()) {
                                Text("Cancelar")
                            }
                        } else {
                            OutlinedButton(onClick = { onEdit(recipe) }, modifier = Modifier.fillMaxWidth()) {
                                Text("Editar")
                            }
                        }
                        OutlinedButton(
                            onClick = { onToggleFavorite(recipe, !recipe.isFavorite) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (recipe.isFavorite) "Remover" else "Favoritar")
                        }
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (isEditing) {
                            OutlinedButton(onClick = onCancelEdit) {
                                Text("Cancelar")
                            }
                        } else {
                            OutlinedButton(onClick = { onEdit(recipe) }) {
                                Text("Editar")
                            }
                        }
                        OutlinedButton(onClick = { onToggleFavorite(recipe, !recipe.isFavorite) }) {
                            Text(if (recipe.isFavorite) "Remover" else "Favoritar")
                        }
                    }
                }
            }
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                if (maxWidth < 320.dp) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = onShareRecipe,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Compartilhar")
                        }
                        OutlinedButton(
                            onClick = { onDelete(recipe) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Excluir")
                        }
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = onShareRecipe,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Compartilhar")
                        }
                        OutlinedButton(
                            onClick = { onDelete(recipe) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Excluir")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EditRecipeBlock(
    uiState: RecipeUiState,
    onFormChange: (RecipeFormState) -> Unit,
    onSaveEdit: () -> Unit
) {
    ModernCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Editar receita", style = MaterialTheme.typography.titleLarge)
            RecipeFormFields(
                uiState = uiState,
                onFormChange = onFormChange
            )
            uiState.message?.let {
                Text(text = it, color = MaterialTheme.colorScheme.primary)
            }
            Button(
                onClick = onSaveEdit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar alteracoes")
            }
        }
    }
}

@Composable
private fun AddIngredientFields(
    uiState: RecipeUiState,
    onIngredientNameChange: (String) -> Unit,
    onIngredientMeasureChange: (String) -> Unit,
    onIngredientObservationChange: (String) -> Unit,
    onAddIngredient: () -> Unit
) {
    OutlinedTextField(
        value = uiState.ingredientName,
        onValueChange = onIngredientNameChange,
        label = { Text("Ingrediente") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
    OutlinedTextField(
        value = uiState.ingredientMeasure,
        onValueChange = onIngredientMeasureChange,
        label = { Text("Quantidade/medida") },
        placeholder = { Text("Ex: 2, 350ml, 1 xicara, a gosto") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
    OutlinedTextField(
        value = uiState.ingredientObservation,
        onValueChange = onIngredientObservationChange,
        label = { Text("Observacao") },
        modifier = Modifier.fillMaxWidth()
    )
    Button(onClick = onAddIngredient, modifier = Modifier.fillMaxWidth()) {
        Text("Adicionar ingrediente")
    }
}

@Composable
private fun DetailBlock(
    title: String,
    text: String
) {
    ModernCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatIngredientText(recipeIngredient: com.lucascosta.receitasdabebel.data.local.entity.RecipeIngredientEntity): String =
    buildString {
        if (recipeIngredient.originalText.isNotBlank()) {
            append(recipeIngredient.originalText)
        } else {
            recipeIngredient.quantity?.let { append(it) }
            if (recipeIngredient.unit.isNotBlank()) append(" ${recipeIngredient.unit}")
        }
        if (recipeIngredient.observation.isNotBlank()) {
            if (isNotBlank()) append("\n")
            append(recipeIngredient.observation)
        }
    }.ifBlank { "Sem detalhes." }
