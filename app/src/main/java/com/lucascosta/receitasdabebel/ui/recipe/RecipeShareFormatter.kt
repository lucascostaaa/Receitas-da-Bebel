package com.lucascosta.receitasdabebel.ui.recipe

import com.lucascosta.receitasdabebel.data.local.entity.RecipeEntity
import com.lucascosta.receitasdabebel.data.local.entity.RecipeIngredientEntity
import com.lucascosta.receitasdabebel.data.local.entity.PreparationStepEntity

fun formatRecipeForSharing(
    recipe: RecipeEntity,
    ingredients: List<RecipeIngredientEntity>,
    steps: List<PreparationStepEntity>,
    ingredientNamesById: Map<Long, String>
): String = buildString {
    appendLine("Confira essa receita: ${recipe.name}")

    if (recipe.description.isNotBlank()) {
        appendLine()
        appendLine(recipe.description)
    }

    if (recipe.preparationTimeMinutes > 0) {
        appendLine()
        appendLine("Tempo: ${recipe.preparationTimeMinutes} min")
    }

    if (ingredients.isNotEmpty()) {
        appendLine()
        appendLine("Ingredientes:")
        ingredients.forEach { ingredient ->
            val name = ingredientNamesById[ingredient.ingredientId] ?: "Ingrediente"
            append("- ")
            append(name)

            val details = formatIngredientDetails(ingredient)
            if (details.isNotBlank()) {
                append(": ")
                append(details)
            }
            appendLine()
        }
    }

    if (steps.isNotEmpty()) {
        appendLine()
        appendLine("Modo de preparo:")
        steps.forEach { step ->
            appendLine("${step.stepNumber}. ${step.description}")
        }
    } else if (recipe.preparationMode.isNotBlank()) {
        appendLine()
        appendLine("Modo de preparo:")
        appendLine(recipe.preparationMode)
    }

    if (recipe.notes.isNotBlank()) {
        appendLine()
        appendLine("Observacoes:")
        appendLine(recipe.notes)
    }

    appendLine()
    appendLine("Abrir receita:")
    appendLine("meuapp://receita/${recipe.id}")
    appendLine()
    appendLine("Link web futuro:")
    appendLine("https://minhasreceitas.app/r/${recipe.id}")
}.trim()

private fun formatIngredientDetails(ingredient: RecipeIngredientEntity): String =
    buildString {
        if (ingredient.originalText.isNotBlank()) {
            append(ingredient.originalText)
            return@buildString
        }

        ingredient.quantity?.let { append(it) }
        if (ingredient.unit.isNotBlank()) {
            if (isNotBlank()) append(" ")
            append(ingredient.unit)
        }
        if (ingredient.observation.isNotBlank()) {
            if (isNotBlank()) append(" - ")
            append(ingredient.observation)
        }
    }
