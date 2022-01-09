package com.example.arthurvanremoortel_werkstuk.ui.explore

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.arthurvanremoortel_werkstuk.RecipeApplication
import com.example.arthurvanremoortel_werkstuk.data.RecipeViewModel
import com.example.arthurvanremoortel_werkstuk.data.RecipeViewModelFactory
import com.example.arthurvanremoortel_werkstuk.data.RecipeWithEverything
import com.example.arthurvanremoortel_werkstuk.databinding.FragmentExploreBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class ExploreFragment : Fragment(), OnItemClickListener {
    private var _binding: FragmentExploreBinding? = null
    private lateinit var auth: FirebaseAuth
    private val recipeViewModel: RecipeViewModel by viewModels {
        RecipeViewModelFactory((activity?.application as RecipeApplication).recipeRepository)
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        auth = Firebase.auth
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = binding.recyclerView
        val adapter = FirebaseRecipeListAdapter(this, requireContext())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
        reloadFirebaseRecipes()
        return root
    }

    /**
     * Currently firebase recipes are only retrieved after onCreateView. Open the the Explore tab again to get new recipes.
     */
    fun reloadFirebaseRecipes(){
        lifecycleScope.launch {
            val succesCallback: (l: List<RecipeWithEverything>) -> Unit = { showFirebaseRecipes(it) }
            recipeViewModel.getFirebaseRecipes(callback = succesCallback)
        }
    }

    fun showFirebaseRecipes(recipesList: List<RecipeWithEverything>): Unit{
        val recyclerView = binding.recyclerView
        val adepter = recyclerView.adapter as FirebaseRecipeListAdapter
        // TODO: Add filters here.
        adepter.submitList(recipesList)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onItemClicked(recipe: RecipeWithEverything) {
        val intent = Intent(this.context, FirebaseRecipeDetailsActivity::class.java)
        intent.putExtra("Recipe", recipe)
        startActivity(intent)
    }
}