package com.lucascosta.receitasdabebel.ui.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucascosta.receitasdabebel.data.local.entity.CategoryEntity
import com.lucascosta.receitasdabebel.data.local.entity.IngredientEntity
import com.lucascosta.receitasdabebel.data.local.entity.PreparationStepEntity
import com.lucascosta.receitasdabebel.data.local.entity.RecipeEntity
import com.lucascosta.receitasdabebel.data.local.entity.RecipeImageEntity
import com.lucascosta.receitasdabebel.data.local.entity.RecipeIngredientEntity
import com.lucascosta.receitasdabebel.data.repository.RecipeRepository
import com.lucascosta.receitasdabebel.data.repository.defaultCategoryNames
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class RecipesViewModel(
    private val repository: RecipeRepository
) : ViewModel() {
    private val selectedRecipeId = MutableStateFlow<Long?>(null)
    private val searchQuery = MutableStateFlow("")
    private val form = MutableStateFlow(RecipeFormState())
    private val ingredientName = MutableStateFlow("")
    private val ingredientMeasure = MutableStateFlow("")
    private val ingredientObservation = MutableStateFlow("")
    private val imagePath = MutableStateFlow("")
    private val stepDescription = MutableStateFlow("")
    private val message = MutableStateFlow<String?>(null)

    private val selectedRecipe = selectedRecipeId.flatMapLatest { id ->
        id?.let(repository::observeRecipe) ?: flowOf(null)
    }

    private val selectedIngredients = selectedRecipeId.flatMapLatest { id ->
        id?.let(repository::observeRecipeIngredients) ?: flowOf(emptyList())
    }

    private val selectedImages = selectedRecipeId.flatMapLatest { id ->
        id?.let(repository::observeRecipeImages) ?: flowOf(emptyList())
    }

    private val selectedSteps = selectedRecipeId.flatMapLatest { id ->
        id?.let(repository::observePreparationSteps) ?: flowOf(emptyList())
    }

    private val visibleRecipes = searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.observeRecipes()
            } else {
                repository.searchRecipes(query)
            }
        }

    private val baseData = combine(
        visibleRecipes,
        repository.observeFavoriteRecipes(),
        repository.observeCategories(),
        repository.observeIngredients()
    ) { recipes, favorites, categories, ingredients ->
        RecipeBaseData(
            recipes = recipes,
            favoriteRecipes = favorites,
            categories = categories,
            ingredients = ingredients
        )
    }

    private val selectedData = combine(
        selectedRecipe,
        selectedIngredients,
        selectedImages,
        selectedSteps
    ) { recipe, ingredients, images, steps ->
        SelectedRecipeData(
            recipe = recipe,
            ingredients = ingredients,
            images = images,
            steps = steps
        )
    }

    private val ingredientDraft = combine(
        ingredientName,
        ingredientMeasure,
        ingredientObservation
    ) { name, measure, observation ->
        IngredientDraftData(
            name = name,
            measure = measure,
            observation = observation
        )
    }

    private val formData = combine(
        form,
        imagePath,
        stepDescription,
        message
    ) { recipeForm, currentImagePath, currentStepDescription, currentMessage ->
        RecipeFormData(
            form = recipeForm,
            imagePath = currentImagePath,
            stepDescription = currentStepDescription,
            message = currentMessage
        )
    }

    val uiState = combine(
        baseData,
        searchQuery,
        selectedData,
        ingredientDraft,
        formData
    ) { base, query, selected, ingredient, formData ->
        RecipeUiState(
            recipes = base.recipes,
            favoriteRecipes = base.favoriteRecipes,
            categories = base.categories,
            ingredients = base.ingredients,
            searchQuery = query,
            selectedRecipe = selected.recipe,
            selectedIngredients = selected.ingredients,
            selectedImages = selected.images,
            selectedSteps = selected.steps,
            form = formData.form,
            ingredientName = ingredient.name,
            ingredientMeasure = ingredient.measure,
            ingredientObservation = ingredient.observation,
            imagePath = formData.imagePath,
            stepDescription = formData.stepDescription,
            message = formData.message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RecipeUiState()
    )

    init {
        viewModelScope.launch {
            repository.addCategories(defaultCategoryNames.map { CategoryEntity(name = it) })
        }
    }

    fun selectRecipe(id: Long) {
        selectedRecipeId.value = id
        message.value = null
    }

    fun updateSearchQuery(value: String) {
        searchQuery.value = value
    }

    fun startNewRecipe() {
        form.value = RecipeFormState()
        selectedRecipeId.value = null
        message.value = null
    }

    fun startEditRecipe(recipe: RecipeEntity) {
        selectedRecipeId.value = recipe.id
        form.value = RecipeFormState(
            id = recipe.id,
            name = recipe.name,
            categoryId = recipe.categoryId,
            description = recipe.description,
            preparationMode = recipe.preparationMode,
            preparationTimeMinutes = recipe.preparationTimeMinutes.takeIf { it > 0 }?.toString() ?: "",
            notes = recipe.notes
        )
        message.value = null
    }

    fun updateForm(value: RecipeFormState) {
        form.value = value
        message.value = null
    }

    fun saveRecipe(onSaved: (Long) -> Unit) {
        val currentForm = form.value
        val name = currentForm.name.trim()

        if (name.isBlank()) {
            message.value = "Digite o nome da receita."
            return
        }

        val preparationTime = currentForm.preparationTimeMinutes.trim().toIntOrNull() ?: 0

        viewModelScope.launch {
            val savedId = currentForm.id
            if (savedId == null) {
                repository.addRecipe(
                    RecipeEntity(
                        name = name,
                        categoryId = currentForm.categoryId,
                        description = currentForm.description.trim(),
                        preparationMode = currentForm.preparationMode.trim(),
                        preparationTimeMinutes = preparationTime,
                        notes = currentForm.notes.trim()
                    )
                )
            } else {
                val existingRecipe = uiState.value.selectedRecipe
                repository.updateRecipe(
                    (existingRecipe ?: RecipeEntity(
                        id = savedId,
                        name = name
                    )).copy(
                        name = name,
                        categoryId = currentForm.categoryId,
                        description = currentForm.description.trim(),
                        preparationMode = currentForm.preparationMode.trim(),
                        preparationTimeMinutes = preparationTime,
                        notes = currentForm.notes.trim()
                    )
                )
                savedId
            }.also { id ->
                selectedRecipeId.value = id
                form.value = RecipeFormState()
                message.value = "Receita salva."
                onSaved(id)
            }
        }
    }

    fun deleteRecipe(recipe: RecipeEntity, onDeleted: () -> Unit) {
        viewModelScope.launch {
            repository.deleteRecipe(recipe)
            selectedRecipeId.value = null
            message.value = "Receita excluida."
            onDeleted()
        }
    }

    fun setFavorite(recipe: RecipeEntity, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.setRecipeFavorite(recipe.id, isFavorite)
        }
    }

    fun updateIngredientName(value: String) {
        ingredientName.value = value
        message.value = null
    }

    fun updateIngredientMeasure(value: String) {
        ingredientMeasure.value = value
    }

    fun updateIngredientObservation(value: String) {
        ingredientObservation.value = value
    }

    fun addIngredientToSelectedRecipe() {
        val recipeId = selectedRecipeId.value ?: return
        val name = ingredientName.value.trim()

        if (name.isBlank()) {
            message.value = "Digite o nome do ingrediente."
            return
        }

        viewModelScope.launch {
            val ingredientId = repository.getOrCreateIngredientId(name)
            if (ingredientId <= 0) {
                message.value = "Nao foi possivel cadastrar o ingrediente."
                return@launch
            }

            repository.addRecipeIngredient(
                RecipeIngredientEntity(
                    recipeId = recipeId,
                    ingredientId = ingredientId,
                    originalText = ingredientMeasure.value.trim(),
                    observation = ingredientObservation.value.trim(),
                    sortOrder = uiState.value.selectedIngredients.size
                )
            )
            ingredientName.value = ""
            ingredientMeasure.value = ""
            ingredientObservation.value = ""
            message.value = "Ingrediente adicionado."
        }
    }

    fun updateImagePath(value: String) {
        imagePath.value = value
        message.value = null
    }

    fun addImageToSelectedRecipe() {
        val recipeId = selectedRecipeId.value ?: return
        val path = imagePath.value.trim()

        if (path.isBlank()) {
            message.value = "Informe o caminho da imagem."
            return
        }

        viewModelScope.launch {
            repository.addRecipeImage(
                RecipeImageEntity(
                    recipeId = recipeId,
                    imagePath = path,
                    sortOrder = uiState.value.selectedImages.size
                )
            )
            imagePath.value = ""
            message.value = "Imagem anexada."
        }
    }

    fun updateStepDescription(value: String) {
        stepDescription.value = value
        message.value = null
    }

    fun addPreparationStepToSelectedRecipe() {
        val recipeId = selectedRecipeId.value ?: return
        val description = stepDescription.value.trim()

        if (description.isBlank()) {
            message.value = "Digite a descricao do passo."
            return
        }

        viewModelScope.launch {
            repository.addPreparationStep(
                PreparationStepEntity(
                    recipeId = recipeId,
                    stepNumber = uiState.value.selectedSteps.size + 1,
                    description = description
                )
            )
            stepDescription.value = ""
            message.value = "Passo adicionado."
        }
    }
}

private data class RecipeBaseData(
    val recipes: List<RecipeEntity>,
    val favoriteRecipes: List<RecipeEntity>,
    val categories: List<CategoryEntity>,
    val ingredients: List<IngredientEntity>
)

private data class SelectedRecipeData(
    val recipe: RecipeEntity?,
    val ingredients: List<RecipeIngredientEntity>,
    val images: List<RecipeImageEntity>,
    val steps: List<PreparationStepEntity>
)

private data class IngredientDraftData(
    val name: String,
    val measure: String,
    val observation: String
)

private data class RecipeFormData(
    val form: RecipeFormState,
    val imagePath: String,
    val stepDescription: String,
    val message: String?
)
