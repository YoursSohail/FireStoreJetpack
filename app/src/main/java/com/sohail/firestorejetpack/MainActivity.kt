package com.sohail.firestorejetpack

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sohail.firestorejetpack.ui.theme.FirestoreJetpackTheme

class MainActivity : ComponentActivity() {

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirestoreJetpackTheme {
                Surface(color = MaterialTheme.colors.background) {
                    RecipeForm()
                }
            }
        }
    }

    @Composable
    fun RecipeForm() {
        val context = LocalContext.current
        val titleValue = remember { mutableStateOf(TextFieldValue()) }
        val ingredientsValue = remember { mutableStateOf(TextFieldValue()) }
        val processValue = remember { mutableStateOf(TextFieldValue()) }
        val recipes = remember { mutableStateListOf(Recipe()) }
        val isSaved = remember { mutableStateOf(false) }

        RecipeService.getRecipes(recipes)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            DetailInput(fieldValue = titleValue, label = "Title")
            DetailInput(fieldValue = ingredientsValue, label = "Ingredients", singleLine = false)
            DetailInput(fieldValue = processValue, label = "Process", singleLine = false)
            Spacer(modifier = Modifier.padding(4.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {

                    val recipe = Recipe(
                        title = titleValue.value.text,
                        ingredients = ingredientsValue.value.text,
                        process = processValue.value.text
                    )

                    db.collection("recipes")
                        .add(recipe)
                        .addOnCompleteListener{
                            if(it.isSuccessful) {
                                showToast(context,"Saved!")
                                isSaved.value = true
                                processValue.value = TextFieldValue()
                                titleValue.value = TextFieldValue()
                                ingredientsValue.value = TextFieldValue()
                            } else {
                                showToast(context,"Error saving")
                            }
                        }
                        .addOnFailureListener{
                            showToast(context,"Error: ${it.message}")
                        }


                }) {
                Text(text = "Save")
            }
            if(isSaved.value) {
                RecipeService.getRecipes(recipes)
                isSaved.value = false
            }
            Spacer(modifier = Modifier.padding(8.dp))
            LazyRow {
                items(recipes) { item: Recipe ->
                    RecipeListItem(recipe = item)
                }
            }

        }
    }

    @Composable
    fun RecipeListItem(recipe: Recipe) {
        Card(
            modifier = Modifier.padding(end = 8.dp),
            shape = RoundedCornerShape(4.dp),
            elevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = recipe.title)
                Spacer(modifier = Modifier.padding(8.dp))
                Text(text = recipe.ingredients)
                Spacer(modifier = Modifier.padding(8.dp))
                Text(text = recipe.process)
            }
        }
    }

    private fun showToast(context: Context,msg: String) {
        Toast.makeText(
            context,
            msg,
            Toast.LENGTH_SHORT
        ).show()
    }

    @Composable
    fun DetailInput(
        fieldValue: MutableState<TextFieldValue>,
        label: String,
        singleLine: Boolean = true
    ) {
        OutlinedTextField(
            label = {
                Text(text = label)
            },
            modifier = if (singleLine) {
                Modifier.fillMaxWidth()
            } else {
                Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            },
            singleLine = singleLine,
            value = fieldValue.value,
            onValueChange = {
                fieldValue.value = it
            })
    }

}

fun <T> SnapshotStateList<T>.updateList(newList: List<T>){
    clear()
    addAll(newList)
}

