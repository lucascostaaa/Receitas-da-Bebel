package com.lucascosta.receitasdabebel.ui.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lucascosta.receitasdabebel.ui.components.ModernCard
import com.lucascosta.receitasdabebel.ui.components.ScreenTopBar

@Composable
fun RecipeFormScreen(
    uiState: RecipeUiState,
    onBack: () -> Unit,
    onFormChange: (RecipeFormState) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val form = uiState.form

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ScreenTopBar(
            title = if (form.id == null) "Cadastrar receita" else "Editar receita",
            subtitle = "Preencha os dados principais",
            action = {
                Button(onClick = onBack) {
                    Text("Voltar")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 22.dp)
        )
        ModernCard(modifier = Modifier.fillMaxWidth()) {
            RecipeFormFields(
                uiState = uiState,
                onFormChange = onFormChange,
                modifier = Modifier.padding(16.dp)
            )
        }
        uiState.message?.let {
            Text(text = it, color = MaterialTheme.colorScheme.primary)
        }
        Button(
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 120.dp)
        ) {
            Text("Salvar receita")
        }
    }
}

@Composable
fun RecipeFormFields(
    uiState: RecipeUiState,
    onFormChange: (RecipeFormState) -> Unit,
    modifier: Modifier = Modifier
) {
    val form = uiState.form

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = form.name,
            onValueChange = { onFormChange(form.copy(name = it)) },
            label = { Text("Nome da receita") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = form.description,
            onValueChange = { onFormChange(form.copy(description = it)) },
            label = { Text("Descricao curta") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )
        Text(text = "Categoria", style = MaterialTheme.typography.titleMedium)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = form.categoryId == null,
                onClick = { onFormChange(form.copy(categoryId = null)) },
                label = { Text("Sem categoria") }
            )
            uiState.categories.forEach { category ->
                FilterChip(
                    selected = form.categoryId == category.id,
                    onClick = { onFormChange(form.copy(categoryId = category.id)) },
                    label = { Text(category.name) }
                )
            }
        }
        OutlinedTextField(
            value = form.preparationTimeMinutes,
            onValueChange = { onFormChange(form.copy(preparationTimeMinutes = it)) },
            label = { Text("Tempo de preparo em minutos") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = form.preparationMode,
            onValueChange = { onFormChange(form.copy(preparationMode = it)) },
            label = { Text("Modo de preparo") },
            minLines = 5,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = form.notes,
            onValueChange = { onFormChange(form.copy(notes = it)) },
            label = { Text("Observacoes") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
