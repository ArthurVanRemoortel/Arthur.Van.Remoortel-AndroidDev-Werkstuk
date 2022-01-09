package com.example.arthurvanremoortel_werkstuk.data

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.ImageView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.io.*

// Primary purpose is passing images between intents since images can be to large to to included with the intent.
object ImageCache {
    private var cachedImages: HashMap<String, Bitmap> = HashMap()

    fun getCachedImage(imageId: String): Bitmap? {
        return cachedImages[imageId]
    }

    fun addCachedImage(imageId: String, image: Bitmap) {
        cachedImages[imageId] = image

    }

    fun deleteCachedImage(imageId: String) {
        cachedImages.remove(imageId)
    }


}

class ImageStorage(val context: Context) {

    fun getImagePath(fileName: String): String {
        val wrapper = ContextWrapper(context.applicationContext)
        var file = wrapper.getDir("images", Context.MODE_PRIVATE)
        file = File(file, "${fileName}.jpg")
        return file.path
    }

    fun getImageFromInternalStorage(fileName: String): Bitmap {
        val path = getImagePath(fileName)
        val options = BitmapFactory.Options()
        val bitmap = BitmapFactory.decodeFile(path, options)
        return bitmap
    }

    // Source: https://android--code.blogspot.com/2018/04/android-kotlin-save-image-to-internal.html
    fun saveImageToInternalStorage(bitmap:Bitmap, fileName: String): Uri {
        val wrapper = ContextWrapper(context.applicationContext)
        var file = wrapper.getDir("images", Context.MODE_PRIVATE)
        file = File(file, "${fileName}.jpg")
        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()

            stream.close()
        } catch (e: IOException){
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)
    }

    fun uploadToFirebase(imageName: String, uploadName: String){
        val firebaseImageRef = Firebase.storage.reference.child("images/${uploadName}.jpg")
        val img = ImageStorage(context).getImageFromInternalStorage(imageName)

        val baos = ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        val data = baos.toByteArray()
        firebaseImageRef.putBytes(data)
    }
    fun getFirebaseImageReference(imageName: String): StorageReference {
        val firebaseImageRef = Firebase.storage.reference.child("images/${imageName}.jpg")
        return firebaseImageRef
    }

    fun trySetImageViewFromFirebase(imageView: ImageView, imageName: String) {
        val fbref = getFirebaseImageReference(imageName)
        fbref.downloadUrl.addOnSuccessListener {
            Picasso.get().load(it.toString()).into(imageView)
        }
    }
}