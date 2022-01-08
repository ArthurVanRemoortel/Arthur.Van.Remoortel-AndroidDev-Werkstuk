package com.example.arthurvanremoortel_werkstuk.ui.recipes

import android.R.attr
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.EditText
import android.widget.Toast
import com.example.arthurvanremoortel_werkstuk.R
import com.example.arthurvanremoortel_werkstuk.data.Recipe
import com.example.arthurvanremoortel_werkstuk.data.RecipeWithEverything
import com.example.arthurvanremoortel_werkstuk.databinding.ActivityNewRecipeBinding
import android.R.attr.data

import java.io.InputStream
import android.R.attr.data
import android.net.Uri
import android.R.attr.data





class NewRecipeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewRecipeBinding
    private lateinit var editWordView: EditText
    private var existingRecipeWithEverything: RecipeWithEverything? = null
    private var selectedImageBitmap: Bitmap? = null
    private val pickImageRequestCode = 1
    private val imageCaptureRequestCode = 2

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        editWordView = findViewById(R.id.editTitleTextView)
        existingRecipeWithEverything = intent.getParcelableExtra<RecipeWithEverything>("Recipe")//.e as RecipeWithEverything

        fillDataFromCurrentRecipe()

        binding.choosePhotoFab.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), pickImageRequestCode)
        }
        binding.takePhotoFab.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                startActivityForResult(takePictureIntent, imageCaptureRequestCode)
            } catch (e: ActivityNotFoundException) {
                // display error state to the user
            }

        }

        binding.saveFab.setOnClickListener{
            val replyIntent = Intent()
            if (TextUtils.isEmpty(editWordView.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val title = binding.editTitleTextView.text.toString().trim()
                val rating = binding.rating.rating
                var duration = binding.editRecipeDurationText.text.toString().trim().toIntOrNull()
                if (duration == null){
                    duration = 0 //TODO: Temporary fix.
                }
                replyIntent.putExtra("title", title)
                replyIntent.putExtra("rating", rating)
                replyIntent.putExtra("duration", duration)
                replyIntent.putExtra("currentRecipe", existingRecipeWithEverything!!)
                setResult(Activity.RESULT_OK, replyIntent)

            }
            finish()
        }
    }


    private fun fillDataFromCurrentRecipe(){
        existingRecipeWithEverything?.let {
            binding.editTitleTextView.setText(it.recipe.title)
            binding.rating.rating = it.recipe.rating.toFloat()

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == imageCaptureRequestCode && resultCode == RESULT_OK) {
            selectedImageBitmap = data!!.extras!!.get("data") as Bitmap // TODO: Are these !! ok?
            binding.editImageView.setImageBitmap(selectedImageBitmap)

        } else if (requestCode == pickImageRequestCode && resultCode == RESULT_OK) {
            if (data == null) {
                return
            } else {
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, data.data)
                binding.editImageView.setImageBitmap(selectedImageBitmap)
            }
        }
    }


    companion object {
        const val EXTRA_REPLY = "com.example.android.wordlistsql.REPLY"
    }
}