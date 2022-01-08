package com.example.arthurvanremoortel_werkstuk.ui.explore

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.arthurvanremoortel_werkstuk.R
import com.example.arthurvanremoortel_werkstuk.RecipeApplication
import com.example.arthurvanremoortel_werkstuk.data.*
import com.example.arthurvanremoortel_werkstuk.databinding.ActivityFirebaseRecipeDetailsBinding
import com.example.arthurvanremoortel_werkstuk.ui.recipes.NewRecipeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class FirebaseRecipeDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFirebaseRecipeDetailsBinding
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        binding = ActivityFirebaseRecipeDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        recipeWithEverything = intent.getParcelableExtra<RecipeWithEverything>("Recipe") as RecipeWithEverything
        updateGui()
        updateSaveFab()
    }

    fun updateGui(){
        binding.recipeTitleText.text = recipeWithEverything.recipe.title
        binding.recipeDurationText.text = recipeWithEverything.recipe.preparation_duration_minutes.toString()

        if (isRecipeByCurrentUser()) {
            binding.saveFab.visibility = View.INVISIBLE
        }

        binding.saveFab.setOnClickListener {
            if (!isRecipeByCurrentUser()) {
                saveRecipe()
            } else {
                // TODO: Error cannot save your own recipe. Should not happen if I don't show the save button in this case.
            }
        }
    }

    private fun updateSaveFab() {
        binding.saveFab.setImageResource(R.drawable.ic_baseline_save_alt_24)
    }

    private fun shareRecipe(){
        val database = Firebase.database
        val ref = database.reference.child("recipes").push()
        ref.setValue(recipeWithEverything.toMap()).addOnSuccessListener {
            recipeWithEverything.recipe.firebaseId = ref.key
            recipeViewModel.update(recipeWithEverything.recipe)
            updateSaveFab()
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
            updateSaveFab()
        }
    }

    private fun removeSavedRecipe(){
        lifecycleScope.launch{
            recipeWithEverything.recipe.recipeId?.let {
                recipeViewModel.repository.recipeDao.deleteByUserId(
                    it
                )
            }
            updateSaveFab()
            finish()
        }
    }
}