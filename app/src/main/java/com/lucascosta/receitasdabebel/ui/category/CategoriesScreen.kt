package com.lucascosta.receitasdabebel.ui.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lucascosta.receitasdabebel.data.local.entity.CategoryEntity
import com.lucascosta.receitasdabebel.ui.components.ModernCard
import com.lucascosta.receitasdabebel.ui.components.ResponsiveContent
import com.lucascosta.receitasdabebel.ui.components.ScreenTopBar
import com.lucascosta.receitasdabebel.ui.theme.ReceitasDaBebelTheme

@Composable
fun CategoriesScreen(
    viewModel: CategoriesViewModel,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CategoriesContent(
        uiState = uiState,
        onCategoryNameChange = viewModel::onCategoryNameChange,
        onAddCategory = viewModel::addCategory,
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
fun CategoriesContent(
    uiState: CategoriesUiState,
    onCategoryNameChange: (String) -> Unit,
    onAddCategory: () -> Unit,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        ResponsiveContent(modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                ScreenTopBar(
                    title = "Categorias",
                    subtitle = "Organize as receitas por tipo",
                    action = {
                        onBack?.let {
                            OutlinedButton(onClick = it) {
                                Text("Inicio")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 22.dp)
                )
                ModernCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.categoryName,
                            onValueChange = onCategoryNameChange,
                            label = { Text("Nome da categoria") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = onAddCategory,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Cadastrar categoria")
                        }
                        uiState.message?.let { message ->
                            Text(
                                text = message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                Text(
                    text = "Categorias cadastradas",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(
                        items = uiState.categories,
                        key = CategoryEntity::id
                    ) { category ->
                        ModernCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoriesContentPreview() {
    ReceitasDaBebelTheme {
        CategoriesContent(
            uiState = CategoriesUiState(
                categories = listOf(
                    CategoryEntity(id = 1, name = "Bolos"),
                    CategoryEntity(id = 2, name = "Tortas"),
                    CategoryEntity(id = 3, name = "Massas")
                )
            ),
            onCategoryNameChange = {},
            onAddCategory = {}
        )
    }
}
