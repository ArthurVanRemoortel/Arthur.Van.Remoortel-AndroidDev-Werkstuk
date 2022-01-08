package com.example.arthurvanremoortel_werkstuk.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.arthurvanremoortel_werkstuk.R
import com.example.arthurvanremoortel_werkstuk.RecipeApplication
import com.example.arthurvanremoortel_werkstuk.data.*
import com.example.arthurvanremoortel_werkstuk.databinding.ActivityRecipeDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class RecipeDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecipeDetailsBinding
    private lateinit var auth: FirebaseAuth

    private lateinit var recipeWithEverything: RecipeWithEverything

    private val recipeViewModel: RecipeViewModel by viewModels {
        RecipeViewModelFactory((application as RecipeApplication).recipeRepository)
    }
    private val ingredientViewModel: IngredientViewModel by viewModels {
        IngredientViewModelFactory((application as RecipeApplication).ingredientsRepository)
    }
    private val preparationStepViewModel: PreparationStepViewModel by viewModels {
        PreparationStepViewModelFactory((application as RecipeApplication).preparationStepRepository)
    }


    private fun isRecipeByCurrentUser(): Boolean {
        return recipeWithEverything.recipe.isRecipeByUser(auth.currentUser!!) // TODO: auth.currentUser should never be null be find better solution.
    }
    private fun isRecipeOnFirebase(): Boolean {
        return recipeWithEverything.recipe.isRecipeOnFirebase()
    }
    private fun isRecipeSavedLocally(): Boolean {
        return recipeWithEverything.recipe.isRecipeSavedLocally()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        binding = ActivityRecipeDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        recipeWithEverything = intent.getParcelableExtra<RecipeWithEverything>("Recipe") as RecipeWithEverything

        binding.recipeTitleText.text = recipeWithEverything.recipe.title
        binding.recipeDurationText.text = recipeWithEverything.recipe.preparation_duration_minutes.toString()
        binding.recipeDetailsImage.setImageResource(R.drawable.pizza)
        binding.fab.setOnClickListener {
            handleFabClick()
        }
        updateFab()
    }

    private fun handleFabClick(){
        if (!isRecipeOnFirebase()) {
            // Local private recipe not on firebase.
            shareRecipe()
        } else {
            // Exists on firebase.
            if (!isRecipeByCurrentUser()) {
                // Recipe is on firebase but not by current user.
                if (isRecipeSavedLocally()){
                    // Saved recipe by other user.
                    removeSavedRecipe()
                } else {
                    // Not saved recipe by other user.
                    saveRecipe()
                }

            } else {
                // Recipe is on firebase and is created nu current user.
                stopShareRecipe()
            }
        }
    }

    private fun updateFab() {
        if (!isRecipeOnFirebase()) {
            // Local private recipe not on firebase.
            binding.fab.setImageResource(R.drawable.ic_baseline_share_24)
        } else {
            // Exists on firebase.
            if (!isRecipeByCurrentUser()) {
                // Recipe is on firebase but not by current user.
                if (isRecipeSavedLocally()){
                    // Saved recipe by other user.
                    binding.fab.setImageResource(R.drawable.ic_baseline_delete_24)
                } else {
                    // Not saved recipe by other user.
                    binding.fab.setImageResource(R.drawable.ic_baseline_save_alt_24)
                }

            } else {
                // Recipe is on firebase and is created nu current user.
                binding.fab.setImageResource(R.drawable.ic_baseline_cloud_off_24)
            }
        }

    }

    private fun shareRecipe(){
        val database = Firebase.database
        val ref = database.reference.child("recipes").push()
        ref.setValue(recipeWithEverything.toMap()).addOnSuccessListener {
            recipeWithEverything.recipe.firebaseId = ref.key
            recipeViewModel.update(recipeWithEverything.recipe)
            updateFab()
        }
    }

    private fun stopShareRecipe(){
        val database = Firebase.database
        recipeWithEverything.recipe.firebaseId?.let {
            database.reference.child("recipes").child(it).removeValue() .addOnSuccessListener {
                recipeWithEverything.recipe.firebaseId = null
                recipeViewModel.update(recipeWithEverything.recipe)
                updateFab()
            }
        }
    }

    private fun removeSavedRecipe(){
        lifecycleScope.launch{
            recipeWithEverything.recipe.recipeId?.let {
                recipeViewModel.repository.recipeDao.deleteByUserId(
                    it
                )
            }
            updateFab()
        }
    }


    private fun saveRecipe(){
        val recipe = recipeWithEverything.recipe
        lifecycleScope.launch{
            val recipeId = recipeViewModel.repository.recipeDao.insert(recipe)
            recipeWithEverything.ingredients.forEach {
                it.parentRecipeId = recipeId
                ingredientViewModel.insert(it)
            }
            recipeWithEverything.preparationSteps.forEach {
                it.parentRecipeId = recipeId
                preparationStepViewModel.insert(it)
            }
            updateFab()
        }
    }
}