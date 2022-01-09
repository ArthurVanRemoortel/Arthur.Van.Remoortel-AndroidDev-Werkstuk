package com.example.arthurvanremoortel_werkstuk.ui.recipes

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.arthurvanremoortel_werkstuk.R
import com.example.arthurvanremoortel_werkstuk.RecipeApplication
import com.example.arthurvanremoortel_werkstuk.data.*
import com.example.arthurvanremoortel_werkstuk.databinding.FragmentRecipesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class RecipesFragment : Fragment(), OnItemClickListener {
    private var _binding: FragmentRecipesBinding? = null
    private lateinit var auth: FirebaseAuth
    private val newRecipeActivityRequestCode = 1
    private val recipeViewModel: RecipeViewModel by viewModels {
        RecipeViewModelFactory((activity?.application as RecipeApplication).recipeRepository)
    }
    private val ingredientViewModel: IngredientViewModel by viewModels {
        IngredientViewModelFactory((activity?.application as RecipeApplication).ingredientsRepository)
    }
    private val preparationStepViewModel: PreparationStepViewModel by viewModels {
        PreparationStepViewModelFactory((activity?.application as RecipeApplication).preparationStepRepository)
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        auth = Firebase.auth
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val fab = binding.fab
        fab.setOnClickListener {
            val intent = Intent(this.activity, NewRecipeActivity::class.java)
            startActivityForResult(intent, newRecipeActivityRequestCode)
        }
        val recyclerView = binding.recyclerView
        val adapter = RecipeListAdapter(this, requireContext())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        recipeViewModel.allRecipes.observe(viewLifecycleOwner, { recipes ->
            // Update the cached copy of the words in the adapter.
            recipes?.let { adapter.submitList(it) }
        })
        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var failed = false
        if (requestCode == newRecipeActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.getBooleanExtra(NewRecipeActivity.SUCCESS_REPLY, false)?.let {
                val title = data.getStringExtra("title");
                val rating = data.getDoubleExtra("rating", 0.0)
                val duration = data.getIntExtra( "duration", 0);
                val cachedImageUUID  = data.getStringExtra("cachedImageUUID")
                if (title != null){
                    val recipe = Recipe(null, title, rating*2, duration, null, auth.currentUser?.email, false) // TODO: Fail if user is null
//                    recipeViewModel.insert(recipe)


                    lifecycleScope.launch{
                        val recipeId = recipeViewModel.repository.recipeDao.insert(recipe)
                        recipe.recipeId = recipeId
                        val ingredients1 = listOf(
                            Ingredient(null, recipeId, "Dummy ingredient", "0"),
                        )
                        val preparationSteps = listOf(
                            PreparationStep(null, recipeId, "Dummy step.", 10),
                        )
                        for (prepStep in preparationSteps) {
                            preparationStepViewModel.insert(prepStep)
                        }
                        for (ingredient in ingredients1) {
                            ingredientViewModel.insert(ingredient)
                        }
                        if (cachedImageUUID != null) {
                            val image = ImageCache.getCachedImage(cachedImageUUID)
                            image?.let {
                                ImageStorage(requireContext().applicationContext).saveImageToInternalStorage(it, recipeId.toString())
                                recipe.hasImage = true
                                recipeViewModel.update(recipe)
                                ImageCache.deleteCachedImage(cachedImageUUID)
                            }
                        }
                    }
                } else {
                    failed = true
                }
            }
        }
        if (failed){
            Toast.makeText(
                this.activity?.applicationContext,
                getString(R.string.recipe_save_failed_form),
                Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClicked(recipe: RecipeWithEverything) {
        val intent = Intent(this.context, RecipeDetailsActivity::class.java)
        intent.putExtra("Recipe", recipe)
        startActivity(intent)

//        this.findNavController().navigate(R.id.recipe_details_fragment)


    }
}