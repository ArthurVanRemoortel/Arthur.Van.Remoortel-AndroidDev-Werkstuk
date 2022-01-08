package com.example.arthurvanremoortel_werkstuk.data

import android.os.Parcelable
import androidx.room.*
import androidx.room.Embedded
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.Exclude
import com.google.firebase.ktx.Firebase
import kotlinx.parcelize.Parcelize
import java.util.HashMap

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
            "preparation_duration_minutes" to preparation_duration_minutes,
            "firebaseId" to firebaseId,
            "creatorEmail" to creatorEmail,
        )
    }
    fun isRecipeByUser(user: FirebaseUser): Boolean{
        return creatorEmail == user.email
    }

    fun isRemoteRecipeAndNotCurrentUser(user: FirebaseUser) : Boolean{
        return firebaseId != null && !isRecipeByUser(user)
    }

    fun isRecipeOnFirebase() : Boolean{
        return firebaseId != null
    }
    fun isRecipeSavedLocally() : Boolean{
        return recipeId != null
    }

    companion object {
        fun fromFirebaseJson(data: HashMap<String, Any>): Recipe {
            return Recipe(
                recipeId = null,
                title = data.get("title").toString(),
                rating = (data.get("rating") as Long).toInt(),
                preparation_duration_minutes = (data.get("preparation_duration_minutes") as Long).toInt(),
                firebaseId = data.get("firebaseId").toString(),
                creatorEmail = data.get("creatorEmail").toString(),
            )
        }
    }
}

@Parcelize @Entity data class Ingredient(
    @PrimaryKey(autoGenerate = true) val ingredientId: Long?,
    var parentRecipeId: Long?,
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

    companion object {
        fun fromFirebaseJson(data: HashMap<String, Any>): Ingredient {
            return Ingredient(
                ingredientId = null,
                parentRecipeId = null,
                name = data.get("name").toString(),
                amount = data.get("amount").toString()
            )
        }
    }

}

@Parcelize @Entity data class PreparationStep(
    @PrimaryKey(autoGenerate = true) val prepStepId: Long?,
    var parentRecipeId: Long?,
    var description: String,
    var duration_minutes: Int?,
) : Parcelable{
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "prepStepId" to null,
            "description" to description,
            "duration_minutes" to duration_minutes,
        )
    }

    companion object {
        fun fromFirebaseJson(data: HashMap<String, Any>): PreparationStep {
            return PreparationStep(
                prepStepId = null,
                parentRecipeId = null,
                description = data.get("description").toString(),
                duration_minutes = (data.get("duration_minutes") as Long?)?.toInt()
            )
        }
    }
}

@Parcelize data class RecipeWithEverything (
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
    var preparationSteps: List<PreparationStep>,

): Parcelable {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "recipe" to recipe.toMap() ,
            "ingredients" to ingredients.map { it.toMap() } ,
            "preparationSteps" to preparationSteps.map { it.toMap() },
        )
    }

    companion object {
        fun fromFirebaseJson(data: HashMap<String, Any>): RecipeWithEverything {
            val recipeData = data.get("recipe") as HashMap<String, Any>
            val ingredientsData = data.get("ingredients") as ArrayList<HashMap<String, Any>>
            val prepStepsData = data.get("preparationSteps") as ArrayList<HashMap<String, Any>>
            return RecipeWithEverything(
                recipe = Recipe.fromFirebaseJson(recipeData),
                ingredients = ingredientsData.map { Ingredient.fromFirebaseJson(it) },
                preparationSteps = prepStepsData.map { PreparationStep.fromFirebaseJson(it) },
            )
        }
    }
}


