package com.example.arthurvanremoortel_werkstuk.data

import androidx.annotation.WorkerThread
import com.example.arthurvanremoortel_werkstuk.data.Recipe
import com.example.arthurvanremoortel_werkstuk.data.RecipeDao
import kotlinx.coroutines.flow.Flow

class RecipeRepository(val recipeDao: RecipeDao) {

    val allRecipes: Flow<List<RecipeWithEverything>> = recipeDao.getRecipesWithEverything()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(recipe: Recipe) {
        recipeDao.insert(recipe)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(recipe: Recipe) {
        recipe.recipeId?.let { recipeDao.deleteByUserId(it) }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(recipe: Recipe) {
        recipeDao.updateRecipes(recipe)
    }
}