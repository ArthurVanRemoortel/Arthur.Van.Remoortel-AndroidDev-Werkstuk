package com.example.arthurvanremoortel_werkstuk.data

import androidx.annotation.WorkerThread
import com.example.arthurvanremoortel_werkstuk.data.Recipe
import com.example.arthurvanremoortel_werkstuk.data.RecipeDao
import kotlinx.coroutines.flow.Flow


class IngredientRepository(private val ingredientDao: IngredientDao) {

    val allIngredients: Flow<List<Ingredient>> = ingredientDao.getAlphabetizedIngredients()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(ingredient: Ingredient) {
        ingredientDao.insert(ingredient)
    }
}