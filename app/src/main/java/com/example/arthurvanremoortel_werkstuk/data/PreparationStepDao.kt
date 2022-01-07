package com.example.arthurvanremoortel_werkstuk.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PreparationStepDao {

    @Transaction
    @Query("SELECT * FROM preparationstep")
    fun getPreparationSteps(): Flow<List<PreparationStep>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dish: PreparationStep)

    @Query("DELETE FROM preparationstep")
    suspend fun deleteAll()
}