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
    suspend fun insert(dish: Recipe): Long

    @Query("DELETE FROM recipe")
    suspend fun deleteAll()

    @Query("DELETE FROM recipe WHERE recipeId = :recipeId")
    suspend fun deleteByUserId(recipeId: Long)

    @Update
    suspend fun updateRecipes(vararg recipes: Recipe)

}