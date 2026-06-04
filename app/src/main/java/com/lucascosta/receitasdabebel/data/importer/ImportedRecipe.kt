package com.lucascosta.receitasdabebel.data.importer

data class ImportedRecipe(
    val name: String,
    val category: String = "",
    val description: String = "",
    val preparationMode: String = "",
    val preparationTimeMinutes: Int = 0,
    val notes: String = "",
    val ingredients: List<ImportedIngredient> = emptyList(),
    val steps: List<String> = emptyList(),
    val images: List<String> = emptyList()
)

data class ImportedIngredient(
    val name: String,
    val measure: String = "",
    val observation: String = ""
)
