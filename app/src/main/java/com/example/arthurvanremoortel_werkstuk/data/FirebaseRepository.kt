package com.example.arthurvanremoortel_werkstuk.data

import android.util.Log
import androidx.annotation.WorkerThread
import com.example.arthurvanremoortel_werkstuk.data.Recipe
import com.example.arthurvanremoortel_werkstuk.data.RecipeDao
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FirebaseRepository {

    fun getFirebaseRecipes(): Flow<List<RecipeWithEverything>> {
        val database = Firebase.database

        val firebaseRecipes: Flow<List<RecipeWithEverything>> = flow {
            var recipes: List<RecipeWithEverything> = listOf<RecipeWithEverything>()
            database.reference.child("recipes").get().addOnSuccessListener {
                Log.i("firebase", "Got value ${it.value}")
                val r = it.getValue<List<RecipeWithEverything>>()
                if (r != null) {
                    recipes = r
                }
            }.addOnFailureListener{
                Log.e("firebase", "Error getting data", it)
                recipes = arrayListOf()
            }
            emit(recipes)
        }
        return firebaseRecipes
    }

}