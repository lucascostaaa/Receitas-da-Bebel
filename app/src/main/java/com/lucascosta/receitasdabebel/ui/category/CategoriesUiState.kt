package com.lucascosta.receitasdabebel.ui.category

import com.lucascosta.receitasdabebel.data.local.entity.CategoryEntity

data class CategoriesUiState(
    val categories: List<CategoryEntity> = emptyList(),
    val categoryName: String = "",
    val message: String? = null
)
