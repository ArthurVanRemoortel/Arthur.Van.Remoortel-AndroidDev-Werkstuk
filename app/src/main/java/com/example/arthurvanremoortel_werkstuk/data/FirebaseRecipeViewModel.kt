package com.example.arthurvanremoortel_werkstuk.data

import androidx.lifecycle.*
import kotlinx.coroutines.launch

//class FirebaseRecipeViewModel(private val repository: FirebaseRepository) : ViewModel() {
//
//    val allFirebaseRecipes: LiveData<List<RecipeWithEverything>> = repository.getFirebaseRecipes().asLiveData()
//
//}
//
//class FirebaseRecipeViewModelFactory(private val repository: FirebaseRepository) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(FirebaseRecipeViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return FirebaseRecipeViewModel(repository) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}