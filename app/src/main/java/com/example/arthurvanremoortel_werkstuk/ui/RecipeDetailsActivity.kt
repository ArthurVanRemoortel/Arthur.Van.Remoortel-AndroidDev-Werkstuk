package com.example.arthurvanremoortel_werkstuk.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.arthurvanremoortel_werkstuk.R
import com.example.arthurvanremoortel_werkstuk.data.Recipe
import com.example.arthurvanremoortel_werkstuk.databinding.ActivityLoginBinding
import com.example.arthurvanremoortel_werkstuk.databinding.ActivityRecipeDetailsBinding

class RecipeDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecipeDetailsBinding
    private lateinit var recipe: Recipe

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recipe = intent.getParcelableExtra<Recipe>("Recipe") as Recipe
        binding.recipeTitleText.text = recipe.title
        binding.recipeDurationText.text = recipe.preparation_duration_minutes.toString()
        binding.recipeDetailsImage.setImageResource(R.drawable.pizza)

    }
}