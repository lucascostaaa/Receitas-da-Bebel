package com.lucascosta.receitasdabebel.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lucascosta.receitasdabebel.R
import com.lucascosta.receitasdabebel.ui.components.ModernCard
import com.lucascosta.receitasdabebel.ui.components.ResponsiveContent
import com.lucascosta.receitasdabebel.ui.components.SectionTitle

@Composable
fun HomeScreen(
    onOpenCategories: () -> Unit,
    onOpenRecipes: () -> Unit,
    onCreateRecipe: () -> Unit,
    onOpenFavorites: () -> Unit,
    modifier: Modifier = Modifier
) {
    ResponsiveContent(modifier = modifier) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 28.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Text(
                    text = "Bem Vindo(a), ao Receitas da Bebel!",
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    text = "O que vamos preparar hoje?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            item {
                ModernCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(22.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Receitas da Bebel",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text = "Digitalize o caderno, organize ingredientes e compartilhe com a familia.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Button(onClick = onCreateRecipe) {
                                Text("Cadastrar nova receita")
                            }
                        }
                    }
                }
            }
            item {
                SectionTitle(title = "Atalhos")
            }
            item {
                ShortcutPair(
                    first = {
                        HomeShortcut(
                            title = "Receitas",
                            subtitle = "Listar e buscar",
                            iconRes = R.drawable.ic_shortcut_recipes,
                            onClick = onOpenRecipes
                        )
                    },
                    second = {
                        HomeShortcut(
                            title = "Categorias",
                            subtitle = "Organizar",
                            iconRes = R.drawable.ic_shortcut_categories,
                            onClick = onOpenCategories
                        )
                    }
                )
            }
            item {
                ShortcutPair(
                    first = {
                        HomeShortcut(
                            title = "Favoritos",
                            subtitle = "Mais queridas",
                            iconRes = R.drawable.ic_shortcut_favorites,
                            onClick = onOpenFavorites
                        )
                    },
                    second = {
                        HomeShortcut(
                            title = "Nova",
                            subtitle = "Cadastrar",
                            iconRes = R.drawable.ic_shortcut_add,
                            onClick = onCreateRecipe
                        )
                    }
                )
            }
            item {
                SectionTitle(title = "Proximo passo")
            }
            item {
                ModernCard(
                    onClick = onCreateRecipe,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Adicionar a primeira receita",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Cadastre nome, categoria, modo de preparo e depois anexe ingredientes e fotos.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShortcutPair(
    first: @Composable () -> Unit,
    second: @Composable () -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        if (maxWidth < 360.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.fillMaxWidth()) { first() }
                Box(modifier = Modifier.fillMaxWidth()) { second() }
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.weight(1f)) { first() }
                Box(modifier = Modifier.weight(1f)) { second() }
            }
        }
    }
}

@Composable
private fun HomeShortcut(
    title: String,
    subtitle: String,
    iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModernCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.tertiary)
                    .padding(10.dp)
            ) {
                Image(
                    painter = painterResource(iconRes),
                    contentDescription = title,
                    modifier = Modifier.size(34.dp)
                )
            }
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
