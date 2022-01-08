package com.example.arthurvanremoortel_werkstuk.data

import androidx.annotation.WorkerThread
import com.example.arthurvanremoortel_werkstuk.data.Recipe
import com.example.arthurvanremoortel_werkstuk.data.RecipeDao
import kotlinx.coroutines.flow.Flow


class PreparationStepRepository(val preparationStepDao: PreparationStepDao) {

    val allSteps: Flow<List<PreparationStep>> = preparationStepDao.getPreparationSteps()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(preparationStep: PreparationStep) {
        preparationStepDao.insert(preparationStep)
    }
}