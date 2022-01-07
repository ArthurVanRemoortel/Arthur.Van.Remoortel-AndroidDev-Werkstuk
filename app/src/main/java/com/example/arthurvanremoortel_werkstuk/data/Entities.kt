package com.example.arthurvanremoortel_werkstuk.data

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.room.*
import androidx.room.Embedded
import kotlinx.parcelize.Parcelize
import java.time.Duration

@Entity
@Parcelize
data class Recipe (
    @PrimaryKey(autoGenerate = true) val recipeId: Long?,
    val title: String,
    val rating: Int,
    val is_public: Boolean,
    val preparation_duration_minutes: Int
) : Parcelable

@Entity data class Ingredient(
    @PrimaryKey(autoGenerate = true) val ingredientId: Long?,
    val parentRecipeId: Long,
    val name: String,
    val amount: String,
)

@Entity data class PreparationStep(
    @PrimaryKey(autoGenerate = true) val prepStepId: Long?,
    val parentRecipeId: Long,
    val description: String,
    val duration_minutes: Int?,
)

data class RecipeWithEverything(
    @Embedded val recipe: Recipe,
    @Relation(
        parentColumn = "recipeId",
        entityColumn = "parentRecipeId"
    )
    val ingredients: List<Ingredient>,

    @Relation(
        parentColumn = "recipeId",
        entityColumn = "parentRecipeId"
    )
    val preparationSteps: List<PreparationStep>
)


