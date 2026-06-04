package com.lucascosta.receitasdabebel.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lucascosta.receitasdabebel.data.local.entity.PreparationStepEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PreparationStepDao {
    @Query("SELECT * FROM preparation_steps WHERE recipeId = :recipeId ORDER BY stepNumber")
    fun observeForRecipe(recipeId: Long): Flow<List<PreparationStepEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(step: PreparationStepEntity): Long

    @Delete
    suspend fun delete(step: PreparationStepEntity)
}
