package com.example.arthurvanremoortel_werkstuk.ui.recipes

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.arthurvanremoortel_werkstuk.R
import com.example.arthurvanremoortel_werkstuk.data.ImageStorage
import com.example.arthurvanremoortel_werkstuk.data.RecipeWithEverything


class RecipeListAdapter(val itemClickListener: OnItemClickListener, val context: Context) : ListAdapter<RecipeWithEverything, RecipeListAdapter.RecipeViewHolder>(RecipesComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_recipe, parent, false)
        return RecipeViewHolder(view, context)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, itemClickListener)
    }

    class RecipeViewHolder(itemView: View, val context: Context) : RecyclerView.ViewHolder(itemView) {
        private val recipeTitleView: TextView = itemView.findViewById(R.id.titleTextView)
        private val recipeImageView: ImageView = itemView.findViewById(R.id.recipeImageView)
        private val durationTextView: TextView = itemView.findViewById(R.id.durationTextView)
        private val authorTextView: TextView = itemView.findViewById(R.id.authorTextView)

        fun bind(recipe: RecipeWithEverything, clickListener: OnItemClickListener) {
            recipeTitleView.text = recipe.recipe.title
            durationTextView.text = context.getString(R.string.number_minutes, recipe.recipe.preparation_duration_minutes.toString())
            authorTextView.text = recipe.recipe.creatorEmail
            if (recipe.recipe.hasImage) {
                recipeImageView.setImageBitmap(ImageStorage(context).getImageFromInternalStorage(recipe.recipe.recipeId!!.toString()))
            } else {
                recipeImageView.setImageResource(R.drawable.default_image)
            }
            itemView.setOnClickListener {
                clickListener.onItemClicked(recipe)
            }
        }
    }

    class RecipesComparator : DiffUtil.ItemCallback<RecipeWithEverything>() {
        override fun areItemsTheSame(oldItem: RecipeWithEverything, newItem: RecipeWithEverything): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: RecipeWithEverything, newItem: RecipeWithEverything): Boolean {
            return oldItem.recipe.recipeId == newItem.recipe.recipeId
        }
    }
}

interface OnItemClickListener{
    fun onItemClicked(recipe: RecipeWithEverything)
}