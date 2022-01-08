package com.example.arthurvanremoortel_werkstuk.ui.recipes

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.arthurvanremoortel_werkstuk.RecipeApplication
import com.example.arthurvanremoortel_werkstuk.data.Recipe
import com.example.arthurvanremoortel_werkstuk.data.RecipeViewModel
import com.example.arthurvanremoortel_werkstuk.data.RecipeViewModelFactory
import com.example.arthurvanremoortel_werkstuk.data.RecipeWithEverything
import com.example.arthurvanremoortel_werkstuk.databinding.FragmentRecipesBinding
import com.example.arthurvanremoortel_werkstuk.ui.RecipeDetailsActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RecipesFragment : Fragment(), OnItemClickListener {

    private val newRecipeActivityRequestCode = 1
    private val recipeViewModel: RecipeViewModel by viewModels {
        RecipeViewModelFactory((activity?.application as RecipeApplication).recipeRepository)
    }
//    private lateinit var recipesViewModel: RecipesViewModel
    private var _binding: FragmentRecipesBinding? = null
    private lateinit var auth: FirebaseAuth

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        recipesViewModel =  ViewModelProvider(this).get(RecipesViewModel::class.java)
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        auth = Firebase.auth

        val fab = binding.fab
        fab.setOnClickListener {
            val intent = Intent(this.activity, NewRecipeActivity::class.java)
            startActivityForResult(intent, newRecipeActivityRequestCode)
        }
        val recyclerView = binding.recyclerView
        val adapter = RecipeListAdapter(this)
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

        if (requestCode == newRecipeActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.getStringExtra(NewRecipeActivity.EXTRA_REPLY)?.let {
                val recipe = Recipe(null,"test", 3*2, 20, null, auth.currentUser?.email) // TODO: Fail if user is null
                recipeViewModel.insert(recipe)
            }
        } else {
            Toast.makeText(
                this.activity?.applicationContext,
                "Recipe not saved because form is incorrect.",
                Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClicked(recipe: RecipeWithEverything) {
        Log.d("RECIPE SELECTED", recipe.recipe.title)
        val intent = Intent(this.context, RecipeDetailsActivity::class.java)
        intent.putExtra("Recipe", recipe)
        startActivity(intent)

//        this.findNavController().navigate(R.id.recipe_details_fragment)


    }
}