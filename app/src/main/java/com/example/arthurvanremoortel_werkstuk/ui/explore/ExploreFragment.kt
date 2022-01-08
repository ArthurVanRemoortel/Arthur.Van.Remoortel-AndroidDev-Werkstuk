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
import com.example.arthurvanremoortel_werkstuk.ui.RecipeDetailsActivity
import com.example.arthurvanremoortel_werkstuk.ui.recipes.OnItemClickListener
import com.example.arthurvanremoortel_werkstuk.ui.recipes.RecipeListAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class ExploreFragment : Fragment(), OnItemClickListener {

    private lateinit var dashboardViewModel: ExploreViewModel
    private var _binding: FragmentExploreBinding? = null
    private lateinit var auth: FirebaseAuth

    private val recipeViewModel: RecipeViewModel by viewModels {
        RecipeViewModelFactory((activity?.application as RecipeApplication).recipeRepository)
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        dashboardViewModel = ViewModelProvider(this).get(ExploreViewModel::class.java)
        auth = Firebase.auth
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textDashboard
//        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        val fab = binding.fab
        fab.visibility = View.INVISIBLE

        val recyclerView = binding.recyclerView
        val adapter = RecipeListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
        reloadFirebaseRecipes()
        return root
    }

    fun reloadFirebaseRecipes(){
        lifecycleScope.launch {
            val succesCallback: (l: List<RecipeWithEverything>) -> Unit = { showFirebaseRecipes(it) }
            recipeViewModel.getFirebaseRecipes(callback = succesCallback)
        }
    }

    fun showFirebaseRecipes(recipesList: List<RecipeWithEverything>): Unit{
        val recyclerView = binding.recyclerView
        val adepter = recyclerView.adapter as RecipeListAdapter
        // TODO: Add filters here.
        adepter.submitList(recipesList)
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
    }
}