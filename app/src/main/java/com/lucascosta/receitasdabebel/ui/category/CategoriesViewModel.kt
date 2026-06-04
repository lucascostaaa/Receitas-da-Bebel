package com.lucascosta.receitasdabebel.ui.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucascosta.receitasdabebel.data.local.entity.CategoryEntity
import com.lucascosta.receitasdabebel.data.repository.RecipeRepository
import com.lucascosta.receitasdabebel.data.repository.defaultCategoryNames
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CategoriesViewModel(
    private val repository: RecipeRepository
) : ViewModel() {
    private val categoryName = MutableStateFlow("")
    private val message = MutableStateFlow<String?>(null)

    val uiState = combine(
        repository.observeCategories(),
        categoryName,
        message
    ) { categories, currentName, currentMessage ->
        CategoriesUiState(
            categories = categories,
            categoryName = currentName,
            message = currentMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CategoriesUiState()
    )

    init {
        viewModelScope.launch {
            repository.addCategories(defaultCategoryNames.map { name ->
                CategoryEntity(name = name)
            })
        }
    }

    fun onCategoryNameChange(value: String) {
        categoryName.value = value
        message.value = null
    }

    fun addCategory() {
        val name = categoryName.value.trim()

        if (name.isBlank()) {
            message.value = "Digite o nome da categoria."
            return
        }

        viewModelScope.launch {
            val id = repository.addCategory(CategoryEntity(name = name))

            if (id == INSERT_IGNORED) {
                message.value = "Essa categoria ja existe."
            } else {
                categoryName.value = ""
                message.value = "Categoria cadastrada."
            }
        }
    }

    companion object {
        private const val INSERT_IGNORED = -1L

    }
}
