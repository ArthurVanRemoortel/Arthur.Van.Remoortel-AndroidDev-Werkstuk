package com.example.arthurvanremoortel_werkstuk.data

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import androidx.room.Embedded
import com.google.firebase.database.Exclude
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.time.Duration

@Entity
@Parcelize
data class Recipe (
    @PrimaryKey(autoGenerate = true) val recipeId: Long?,
    var title: String,
    var rating: Int,
    var preparation_duration_minutes: Int,
    var firebaseId: String?,
    var creatorEmail: String?,
) : Parcelable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "title" to title ,
            "rating" to rating,
            "firebaseId" to firebaseId,
            "preparation_duration_minutes" to preparation_duration_minutes,
        )
    }
}

@Parcelize @Entity data class Ingredient(
    @PrimaryKey(autoGenerate = true) val ingredientId: Long?,
    val parentRecipeId: Long,
    var name: String,
    var amount: String,
)  : Parcelable{
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "ingredientId" to null,
            "name" to name,
            "amount" to amount,
        )
    }
}

@Parcelize @Entity data class PreparationStep(
    @PrimaryKey(autoGenerate = true) val prepStepId: Long?,
    val parentRecipeId: Long,
    var description: String,
    var duration_minutes: Int?,
) : Parcelable{
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "prepStepId" to null,
            "name" to description,
            "amount" to duration_minutes,
        )
    }
}

@Parcelize data class RecipeWithEverything(
    @Embedded val recipe: Recipe,
    @Relation(
        parentColumn = "recipeId",
        entityColumn = "parentRecipeId"
    )
    var ingredients : List<Ingredient>,

    @Relation(
        parentColumn = "recipeId",
        entityColumn = "parentRecipeId"
    )
    var preparationSteps: List<PreparationStep>

): Parcelable {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "recipe" to recipe.toMap() ,
            "ingredients" to ingredients.map { it.toMap() } ,
            "preparationSteps" to preparationSteps.map { it.toMap() },
        )
    }

}


