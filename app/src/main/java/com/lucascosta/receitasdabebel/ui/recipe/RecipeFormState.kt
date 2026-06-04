package com.lucascosta.receitasdabebel.ui.recipe

data class RecipeFormState(
    val id: Long? = null,
    val name: String = "",
    val categoryId: Long? = null,
    val description: String = "",
    val preparationMode: String = "",
    val preparationTimeMinutes: String = "",
    val notes: String = ""
)
