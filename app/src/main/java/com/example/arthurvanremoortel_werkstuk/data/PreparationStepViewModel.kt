package com.example.arthurvanremoortel_werkstuk.data

import androidx.lifecycle.*
import kotlinx.coroutines.launch


class PreparationStepViewModel(val repository: PreparationStepRepository) : ViewModel() {

    val allSteps: LiveData<List<PreparationStep>> = repository.allSteps.asLiveData()

    fun insert(preparationStep: PreparationStep) = viewModelScope.launch {
        repository.insert(preparationStep)
    }
}

class PreparationStepViewModelFactory(private val repository: PreparationStepRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PreparationStepViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PreparationStepViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}