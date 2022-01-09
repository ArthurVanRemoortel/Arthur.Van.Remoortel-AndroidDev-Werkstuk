package com.example.arthurvanremoortel_werkstuk.ui.recipes

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
import com.example.arthurvanremoortel_werkstuk.databinding.ActivityRecipeDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import android.graphics.Bitmap


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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        binding = ActivityRecipeDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        recipeWithEverything = intent.getParcelableExtra<RecipeWithEverything>("Recipe") as RecipeWithEverything
        updateGui()
    }

    fun updateGui(){
        binding.recipeTitleText.text = recipeWithEverything.recipe.title
        binding.recipeDurationText.text = recipeWithEverything.recipe.preparation_duration_minutes.toString()
        binding.rating.rating = recipeWithEverything.recipe.rating.toFloat()

        if (recipeWithEverything.recipe.hasImage) {
            binding.recipeDetailsImage.setImageBitmap(ImageStorage(applicationContext).getImageFromInternalStorage(recipeWithEverything.recipe.recipeId!!.toString()))
        } else {
            binding.recipeDetailsImage.setImageResource(R.drawable.default_image)
        }

        binding.authorTextView.text = recipeWithEverything.recipe.creatorEmail

        var ingredientsString = "Ingredients: \n\n"
        for (ingredient in recipeWithEverything.ingredients) {
            ingredientsString += ingredient.amount + ": " + ingredient.name + "\n"
        }
        binding.recipeIngredientsView.text = ingredientsString

        var stepsString = "Steps: \n\n"
        for (step in recipeWithEverything.preparationSteps) {
            stepsString += step.description + "\n\n"
        }
        binding.recipeStepsTextView.text = stepsString

        binding.deleteFab.setOnClickListener {
            removeSavedRecipe()
        }

        if (isRecipeByCurrentUser()) {
            // saveFab needs to be visible and toggle between share or stop share.
            binding.saveFab.setOnClickListener {
                if (isRecipeOnFirebase()) {
                    stopShareRecipe()
                } else {
                    shareRecipe()
                }
            }
            binding.editFab.setOnClickListener {
                val intent = Intent(this, NewRecipeActivity::class.java)
                intent.putExtra("Recipe", recipeWithEverything)
                startActivityForResult(intent, editRecipeActivityRequestCode)
            }
        } else {
            // Not by current user. saveFab, editFab needs to be invisible.
            binding.saveFab.visibility = View.INVISIBLE
            binding.editFab.visibility = View.INVISIBLE
        }
        updateSaveFab()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == editRecipeActivityRequestCode && resultCode == Activity.RESULT_OK) {
            val title = data?.getStringExtra("title");
            val rating = data?.getDoubleExtra("rating", 0.0)
            val duration = data?.getIntExtra( "duration", 0);
            val receivedRecipe = data?.getParcelableExtra<RecipeWithEverything>("currentRecipe")
            val cachedImageUUID  = data?.getStringExtra("cachedImageUUID")
            if (receivedRecipe != null && title != null && rating != null && duration != null){
                receivedRecipe.recipe.title = title
                receivedRecipe.recipe.rating = rating
                receivedRecipe.recipe.preparation_duration_minutes = duration
                if (cachedImageUUID != null) {
                    val image = ImageCache.getCachedImage(cachedImageUUID)
                    image?.let {
                        ImageStorage(applicationContext).saveImageToInternalStorage(it, receivedRecipe.recipe.recipeId!!.toString())
                        receivedRecipe.recipe.hasImage = true
                        ImageCache.deleteCachedImage(cachedImageUUID)
                    }
                }
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

    private fun updateSaveFab() {
        if (!isRecipeOnFirebase()) {
            // Local private recipe not on firebase.
            binding.saveFab.setImageResource(R.drawable.ic_baseline_share_24)
        } else {
            binding.saveFab.setImageResource(R.drawable.ic_baseline_cloud_off_24)
        }
    }

    private fun shareRecipe(){
        val database = Firebase.database
        val ref = database.reference.child("recipes").push()
        ref.setValue(recipeWithEverything.toMap()).addOnSuccessListener {
            val firebaseId = ref.key!!
            recipeWithEverything.recipe.firebaseId = firebaseId
            recipeViewModel.update(recipeWithEverything.recipe)

            if (recipeWithEverything.recipe.hasImage){
                ImageStorage(applicationContext).uploadToFirebase(recipeWithEverything.recipe.recipeId!!.toString(), firebaseId)
            }
            Toast.makeText(
                baseContext, "Recipe shared.",
                Toast.LENGTH_SHORT
            ).show()
            updateSaveFab()
        }
    }

    private fun stopShareRecipe(){
        val database = Firebase.database
        recipeWithEverything.recipe.firebaseId?.let {
            database.reference.child("recipes").child(it).removeValue().addOnSuccessListener {
                recipeWithEverything.recipe.firebaseId = null
                recipeViewModel.update(recipeWithEverything.recipe)
                updateSaveFab()
            }
            Toast.makeText(
                baseContext, "Recipe is not longer shared.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun removeSavedRecipe(){
        lifecycleScope.launch{
            recipeWithEverything.recipe.recipeId?.let {
                if (isRecipeOnFirebase() && isRecipeByCurrentUser()){
                    stopShareRecipe()
                }
                recipeViewModel.repository.recipeDao.deleteByUserId(
                    it
                )
                Toast.makeText(
                    baseContext, "Recipe deleted.",
                    Toast.LENGTH_SHORT
                ).show()
            }

            updateSaveFab()
            finish()
        }

    }
}