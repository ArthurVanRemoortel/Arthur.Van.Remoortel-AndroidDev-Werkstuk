package com.example.arthurvanremoortel_werkstuk.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.arthurvanremoortel_werkstuk.R
import com.example.arthurvanremoortel_werkstuk.RecipeApplication
import com.example.arthurvanremoortel_werkstuk.data.*
import com.example.arthurvanremoortel_werkstuk.databinding.ActivityRecipeDetailsBinding
import com.example.arthurvanremoortel_werkstuk.ui.recipes.NewRecipeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class RecipeDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecipeDetailsBinding
    private lateinit var auth: FirebaseAuth
    private val editRecipeActivityRequestCode = 1

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
        updateGui()
        updateSaveFab()
    }

    fun updateGui(){
        binding.recipeTitleText.text = recipeWithEverything.recipe.title
        binding.recipeDurationText.text = recipeWithEverything.recipe.preparation_duration_minutes.toString()
//        binding.recipeDetailsImage.setImageResource(R.drawable.default_image)
        binding.saveFab.setOnClickListener {
            handleFabClick()
        }

        if (isRecipeSavedLocally() && isRecipeByCurrentUser()){
            binding.editFab.setOnClickListener {
                val intent = Intent(this, NewRecipeActivity::class.java)
                intent.putExtra("Recipe", recipeWithEverything)
                startActivityForResult(intent, editRecipeActivityRequestCode)
            }
        } else {
            binding.editFab.visibility = View.INVISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == editRecipeActivityRequestCode && resultCode == Activity.RESULT_OK) {
            val title = data?.getStringExtra("title");
            val rating = data?.getDoubleExtra("rating", 0.0)
            val duration = data?.getIntExtra( "duration", 0);
            val receivedRecipe = data?.getParcelableExtra<RecipeWithEverything>("currentRecipe")
            if (receivedRecipe != null && title != null && rating != null && duration != null){
                receivedRecipe.recipe.title = title
                receivedRecipe.recipe.rating = rating
                receivedRecipe.recipe.preparation_duration_minutes = duration
                recipeViewModel.update(receivedRecipe.recipe)
                this.recipeWithEverything = receivedRecipe
                updateGui()
            }
            // TODO: Show error.
        } else {
            Toast.makeText(
                this.applicationContext,
                "Recipe not saved because form is incorrect.",
                Toast.LENGTH_LONG).show()
        }
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

    private fun updateSaveFab() {
        if (!isRecipeOnFirebase()) {
            isRecipeSavedLocally()
            // Local private recipe not on firebase.
            binding.saveFab.setImageResource(R.drawable.ic_baseline_share_24)
        } else {
            // Exists on firebase.
            if (!isRecipeByCurrentUser()) {
                // Recipe is on firebase but not by current user.
                if (isRecipeSavedLocally()){
                    // Saved recipe by other user.
                    binding.saveFab.setImageResource(R.drawable.ic_baseline_delete_24)
                } else {
                    // Not saved recipe by other user.
                    binding.saveFab.setImageResource(R.drawable.ic_baseline_save_alt_24)
                }
            } else {
                // Recipe is on firebase and is created by current user.
                binding.saveFab.setImageResource(R.drawable.ic_baseline_cloud_off_24)
            }
        }
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

    private fun stopShareRecipe(){
        val database = Firebase.database
        recipeWithEverything.recipe.firebaseId?.let {
            database.reference.child("recipes").child(it).removeValue() .addOnSuccessListener {
                recipeWithEverything.recipe.firebaseId = null
                recipeViewModel.update(recipeWithEverything.recipe)
                updateSaveFab()
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
            updateSaveFab()
            finish()
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
}