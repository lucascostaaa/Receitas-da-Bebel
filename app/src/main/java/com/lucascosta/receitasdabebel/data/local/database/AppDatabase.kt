package com.lucascosta.receitasdabebel.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lucascosta.receitasdabebel.data.local.dao.CategoryDao
import com.lucascosta.receitasdabebel.data.local.dao.IngredientDao
import com.lucascosta.receitasdabebel.data.local.dao.PreparationStepDao
import com.lucascosta.receitasdabebel.data.local.dao.RecipeDao
import com.lucascosta.receitasdabebel.data.local.dao.RecipeImageDao
import com.lucascosta.receitasdabebel.data.local.dao.RecipeIngredientDao
import com.lucascosta.receitasdabebel.data.local.entity.CategoryEntity
import com.lucascosta.receitasdabebel.data.local.entity.IngredientEntity
import com.lucascosta.receitasdabebel.data.local.entity.PreparationStepEntity
import com.lucascosta.receitasdabebel.data.local.entity.RecipeEntity
import com.lucascosta.receitasdabebel.data.local.entity.RecipeImageEntity
import com.lucascosta.receitasdabebel.data.local.entity.RecipeIngredientEntity

@Database(
    entities = [
        CategoryEntity::class,
        RecipeEntity::class,
        IngredientEntity::class,
        RecipeIngredientEntity::class,
        RecipeImageEntity::class,
        PreparationStepEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun recipeDao(): RecipeDao
    abstract fun ingredientDao(): IngredientDao
    abstract fun recipeIngredientDao(): RecipeIngredientDao
    abstract fun recipeImageDao(): RecipeImageDao
    abstract fun preparationStepDao(): PreparationStepDao

    companion object {
        private const val DATABASE_NAME = "receitas-da-bebel.db"

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { instance = it }
            }
    }
}
