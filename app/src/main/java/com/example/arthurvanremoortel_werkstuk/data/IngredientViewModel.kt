package com.example.arthurvanremoortel_werkstuk.data

import androidx.lifecycle.*
import kotlinx.coroutines.launch


class IngredientViewModel(val repository: IngredientRepository) : ViewModel() {

    val allIngredients: LiveData<List<Ingredient>> = repository.allIngredients.asLiveData()

    fun insert(ingredient: Ingredient) = viewModelScope.launch {
        repository.insert(ingredient)
    }
}

class IngredientViewModelFactory(private val repository: IngredientRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IngredientViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return IngredientViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}