package com.example.arthurvanremoortel_werkstuk.data

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.util.HashMap


class RecipeViewModel(val repository: RecipeRepository) : ViewModel() {
    // TODO: Move some functions scattered around the project to this class.

    val allRecipes: LiveData<List<RecipeWithEverything>> = repository.allRecipes.asLiveData()

    fun insert(recipe: Recipe) = viewModelScope.launch {
        repository.insert(recipe)
    }

    fun update(recipe: Recipe) = viewModelScope.launch {
        repository.update(recipe)
    }
    fun delete(recipe: Recipe) = viewModelScope.launch {
        repository.delete(recipe)
    }

    /** Gets a list of all recipes on firebase.
     *  Calls the callback functions with the list of recipes when succeeded.
     */
    fun getFirebaseRecipes(callback: (f: List<RecipeWithEverything>) -> Unit){
        val database = Firebase.database

        database.reference.child("recipes").get().addOnSuccessListener {
            val recipes: MutableList<RecipeWithEverything> = mutableListOf()
            if (it.value != null) {
                val result: HashMap<String, HashMap<String, Any>>
                result = it.getValue(Any().javaClass) as HashMap<String, HashMap<String, Any>>
                result.forEach { (firebaseId, data) ->
                    val recipeWithEverything = RecipeWithEverything.fromFirebaseJson(data)
                    recipeWithEverything.recipe.firebaseId = firebaseId
                    recipes.add(recipeWithEverything)
                }
                callback(recipes)
            }
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }

}

class RecipeViewModelFactory(private val repository: RecipeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecipeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}