package com.example.arthurvanremoortel_werkstuk.ui

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.arthurvanremoortel_werkstuk.R
import com.example.arthurvanremoortel_werkstuk.RecipeApplication
import com.example.arthurvanremoortel_werkstuk.data.*
import com.example.arthurvanremoortel_werkstuk.databinding.ActivityLoginBinding
import com.example.arthurvanremoortel_werkstuk.databinding.ActivityRecipeDetailsBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class RecipeDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecipeDetailsBinding
    private lateinit var recipe: RecipeWithEverything
    private val recipeViewModel: RecipeViewModel by viewModels {
        RecipeViewModelFactory((application as RecipeApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeDetailsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        recipe = intent.getParcelableExtra<RecipeWithEverything>("Recipe") as RecipeWithEverything
        binding.recipeTitleText.text = recipe.recipe.title
        binding.recipeDurationText.text = recipe.recipe.preparation_duration_minutes.toString()
        binding.recipeDetailsImage.setImageResource(R.drawable.pizza)
        binding.fab.setOnClickListener {
            handleFabClick()
        }
        updateFab()
    }

    private fun handleFabClick(){
        if (recipe.recipe.firebaseId == null) {
            uploadRecipe()
        } else {
            deleteSharedRecipe()
        }
    }

    private fun updateFab(){
        if (recipe.recipe.firebaseId == null) {
            binding.fab.setImageResource(R.drawable.ic_baseline_cloud_queue_24)
        } else {
            binding.fab.setImageResource(R.drawable.ic_baseline_cloud_off_24)
        }
    }

    private fun uploadRecipe(){
        val database = Firebase.database
        val ref = database.reference.child("recipes").push()
        ref.setValue(recipe.toMap()).addOnSuccessListener {
            recipe.recipe.firebaseId = ref.key
            recipeViewModel.update(recipe.recipe)
            updateFab()
        }
    }

    private fun deleteSharedRecipe(){
        val database = Firebase.database
        recipe.recipe.firebaseId?.let {
            database.reference.child("recipes").child(it).removeValue() .addOnSuccessListener {
                recipe.recipe.firebaseId = null
                recipeViewModel.update(recipe.recipe)
                updateFab()
            }
        }
    }
}