package com.example.arthurvanremoortel_werkstuk.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientDao {

    @Query("SELECT * FROM ingredient ORDER BY name ASC")
    fun getAlphabetizedIngredients(): Flow<List<Ingredient>>

    @Transaction
    @Query("SELECT * FROM ingredient")
    fun getIngredients(): Flow<List<Ingredient>>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dish: Ingredient)

    @Query("DELETE FROM ingredient")
    suspend fun deleteAll()
}