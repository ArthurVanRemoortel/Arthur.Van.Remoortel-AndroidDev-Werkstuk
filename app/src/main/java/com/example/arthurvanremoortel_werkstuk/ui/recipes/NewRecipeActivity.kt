package com.example.arthurvanremoortel_werkstuk.ui.recipes

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.EditText
import com.example.arthurvanremoortel_werkstuk.R
import com.example.arthurvanremoortel_werkstuk.data.RecipeWithEverything
import com.example.arthurvanremoortel_werkstuk.databinding.ActivityNewRecipeBinding

import com.example.arthurvanremoortel_werkstuk.data.ImageCache

import java.util.*

/**
 * Save activity is used to create new recipes or edit existing ones.
 */
class NewRecipeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewRecipeBinding
    private var existingRecipeWithEverything: RecipeWithEverything? = null // Has a value when editing a recipe.
    private var photoBitmap: Bitmap? = null
    private val pickImageRequestCode = 1
    private val imageCaptureRequestCode = 2

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
            /**
             * Tries to save the current form input. Can fail if minimum data is not provided.
             */
            val replyIntent = Intent()
            if (TextUtils.isEmpty(binding.editTitleTextView.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val title = binding.editTitleTextView.text.toString().trim()
                val rating = binding.rating.rating.toDouble()
                val duration = binding.editRecipeDurationText.text.toString().trim().toIntOrNull()
                replyIntent.putExtra(SUCCESS_REPLY, true)
                replyIntent.putExtra("title", title)
                replyIntent.putExtra("rating", rating)
                replyIntent.putExtra("duration", duration)
                replyIntent.putExtra("currentRecipe", existingRecipeWithEverything)
                // Images can be to large to put in intents. Use the global ImageCache instead.
                if (photoBitmap != null){
                    val uuid = UUID.randomUUID().toString()
                    replyIntent.putExtra("cachedImageUUID", uuid)
                    ImageCache.addCachedImage(uuid, photoBitmap!!)
                } else {
                    replyIntent.putExtra("cachedImageUUID", null as String?)
                }
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }
    }

    /**
     * Fill form with current value when editing an existing recipe.
     */
    private fun fillDataFromCurrentRecipe(){
        existingRecipeWithEverything?.let {
            binding.editTitleTextView.setText(it.recipe.title)
            binding.rating.rating = it.recipe.rating.toFloat()
            binding.editRecipeDurationText.setText(it.recipe.preparation_duration_minutes.toString())

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == imageCaptureRequestCode && resultCode == RESULT_OK) {
            /** When returning from taking a photo. */
            photoBitmap = data!!.extras!!.get("data") as Bitmap // TODO: Are these !! ok?
            binding.editImageView.setImageBitmap(photoBitmap)

        } else if (requestCode == pickImageRequestCode && resultCode == RESULT_OK) {
            /** When returning selecting an image from your saved images. */
            if (data == null) {
                return
            } else {
                val diskBitmap = MediaStore.Images.Media.getBitmap(contentResolver, data.data)
//                val out = ByteArrayOutputStream()
//                diskBitmap.compress(Bitmap.CompressFormat.JPEG, 1, out)
//                val decoded = BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()))
                photoBitmap = diskBitmap
                binding.editImageView.setImageBitmap(diskBitmap)
            }
        }
    }


    companion object {
        const val SUCCESS_REPLY = "SUCCESS_REPLY"
    }
}