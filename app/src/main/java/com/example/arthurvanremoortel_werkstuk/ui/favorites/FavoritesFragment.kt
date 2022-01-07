package com.example.arthurvanremoortel_werkstuk.ui.favorites

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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.arthurvanremoortel_werkstuk.MainActivityBar
import com.example.arthurvanremoortel_werkstuk.R
import com.example.arthurvanremoortel_werkstuk.RecipeApplication
import com.example.arthurvanremoortel_werkstuk.data.Recipe
import com.example.arthurvanremoortel_werkstuk.data.RecipeViewModel
import com.example.arthurvanremoortel_werkstuk.data.RecipeViewModelFactory
import com.example.arthurvanremoortel_werkstuk.databinding.FragmentFavoritesBinding
import com.example.arthurvanremoortel_werkstuk.ui.RecipeDetailsActivity

class FavoritesFragment : Fragment(), OnItemClickListener {

    private val newRecipeActivityRequestCode = 1
    private val recipeViewModel: RecipeViewModel by viewModels {
        RecipeViewModelFactory((activity?.application as RecipeApplication).repository)
    }

    private lateinit var favoritesViewModel: FavoritesViewModel
    private var _binding: FragmentFavoritesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        favoritesViewModel =  ViewModelProvider(this).get(FavoritesViewModel::class.java)
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)

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

        val root: View = binding.root
        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newRecipeActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.getStringExtra(NewRecipeActivity.EXTRA_REPLY)?.let {
                val recipe = Recipe(null,"test", 3*2, false, 10)
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

    override fun onItemClicked(recipe: Recipe) {
        Log.d("RECIPE SELECTED", recipe.title)
        val intent = Intent(this.context, RecipeDetailsActivity::class.java)
        intent.putExtra("Recipe", recipe)
        startActivity(intent)

//        this.findNavController().navigate(R.id.recipe_details_fragment)


    }
}