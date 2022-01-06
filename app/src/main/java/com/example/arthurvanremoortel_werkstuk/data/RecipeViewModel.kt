package com.example.arthurvanremoortel_werkstuk.data

import androidx.lifecycle.*
import kotlinx.coroutines.launch

class RecipeViewModel(private val repository: ApplicationRepository) : ViewModel() {

    val allRecipes: LiveData<List<Recipe>> = repository.allRecipes.asLiveData()

    fun insert(recipe: Recipe) = viewModelScope.launch {
        repository.insert(recipe)
    }
}

class RecipeViewModelFactory(private val repository: ApplicationRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecipeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}