package com.example.arthurvanremoortel_werkstuk.data

import androidx.annotation.WorkerThread
import com.example.arthurvanremoortel_werkstuk.data.Recipe
import com.example.arthurvanremoortel_werkstuk.data.RecipeDao
import kotlinx.coroutines.flow.Flow

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class ApplicationRepository(private val recipeDao: RecipeDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allRecipes: Flow<List<RecipeWithEverything>> = recipeDao.getRecipesWithEverything()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(recipe: Recipe) {
        recipeDao.insert(recipe)
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(recipe: Recipe) {
        recipeDao.updateRecipes(recipe)
    }
}