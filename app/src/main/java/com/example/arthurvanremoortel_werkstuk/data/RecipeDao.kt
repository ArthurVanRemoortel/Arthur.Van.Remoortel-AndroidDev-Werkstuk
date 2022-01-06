package com.example.arthurvanremoortel_werkstuk.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    @Query("SELECT * FROM recipe_table ORDER BY title ASC")
    fun getAlphabetizedDishes(): Flow<List<Recipe>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dish: Recipe)

    @Query("DELETE FROM recipe_table")
    suspend fun deleteAll()
}