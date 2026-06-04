package com.lucascosta.receitasdabebel.data.importer

import org.json.JSONArray
import org.json.JSONObject

object RecipeImportParser {
    fun parse(rawJson: String): List<ImportedRecipe> {
        val trimmed = rawJson.trim()
        if (trimmed.isBlank()) return emptyList()

        val recipesArray = if (trimmed.startsWith("[")) {
            JSONArray(trimmed)
        } else {
            JSONObject(trimmed).optJSONArray("recipes") ?: JSONArray()
        }

        return buildList {
            for (index in 0 until recipesArray.length()) {
                val item = recipesArray.optJSONObject(index) ?: continue
                val name = item.optString("name").trim()
                if (name.isBlank()) continue

                add(
                    ImportedRecipe(
                        name = name,
                        category = item.optString("category").trim(),
                        description = item.optString("description").trim(),
                        preparationMode = item.optString("preparationMode").trim(),
                        preparationTimeMinutes = item.optInt("preparationTimeMinutes", 0),
                        notes = item.optString("notes").trim(),
                        ingredients = parseIngredients(item.optJSONArray("ingredients")),
                        steps = parseStringArray(item.optJSONArray("steps")),
                        images = parseStringArray(item.optJSONArray("images"))
                    )
                )
            }
        }
    }

    private fun parseIngredients(array: JSONArray?): List<ImportedIngredient> {
        if (array == null) return emptyList()

        return buildList {
            for (index in 0 until array.length()) {
                val item = array.optJSONObject(index)
                if (item == null) {
                    val rawName = array.optString(index).trim()
                    if (rawName.isNotBlank()) add(ImportedIngredient(name = rawName))
                    continue
                }

                val name = item.optString("name").trim()
                if (name.isBlank()) continue

                add(
                    ImportedIngredient(
                        name = name,
                        measure = item.optString("measure").trim(),
                        observation = item.optString("observation").trim()
                    )
                )
            }
        }
    }

    private fun parseStringArray(array: JSONArray?): List<String> {
        if (array == null) return emptyList()

        return buildList {
            for (index in 0 until array.length()) {
                val value = array.optString(index).trim()
                if (value.isNotBlank()) add(value)
            }
        }
    }
}
