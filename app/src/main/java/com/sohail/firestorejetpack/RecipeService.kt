package com.sohail.firestorejetpack

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.firestore.FirebaseFirestore

object RecipeService {

    private val db = FirebaseFirestore.getInstance()
    fun getRecipes(recipes: SnapshotStateList<Recipe>) {
        db.collection("recipes").get().addOnSuccessListener {
            recipes.updateList(it.toObjects(Recipe::class.java))
        }.addOnFailureListener {
            recipes.updateList(listOf())
        }
    }

}