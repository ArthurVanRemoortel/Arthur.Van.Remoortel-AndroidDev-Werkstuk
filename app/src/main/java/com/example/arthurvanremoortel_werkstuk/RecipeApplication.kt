package com.example.arthurvanremoortel_werkstuk

import android.app.Application
import com.example.arthurvanremoortel_werkstuk.data.AppDatabase
import com.example.arthurvanremoortel_werkstuk.data.ApplicationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class RecipeApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { ApplicationRepository(database.recipeDao()) }

}