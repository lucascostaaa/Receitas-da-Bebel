package com.lucascosta.receitasdabebel.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lucascosta.receitasdabebel.data.local.entity.IngredientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientDao {
    @Query("SELECT * FROM ingredients ORDER BY name")
    fun observeAll(): Flow<List<IngredientEntity>>

    @Query("SELECT * FROM ingredients WHERE LOWER(name) = LOWER(:name) LIMIT 1")
    suspend fun findByName(name: String): IngredientEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(ingredient: IngredientEntity): Long
}
