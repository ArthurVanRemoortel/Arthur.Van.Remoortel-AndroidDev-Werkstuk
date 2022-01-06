package com.example.arthurvanremoortel_werkstuk.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    @Query("SELECT * FROM recipe ORDER BY title ASC")
    fun getAlphabetizedRecipes(): Flow<List<Recipe>>

    @Transaction
    @Query("SELECT * FROM recipe")
    fun getRecipesWithEverything(): Flow<List<RecipeWithEverything>>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dish: Recipe)

    @Query("DELETE FROM recipe")
    suspend fun deleteAll()
}