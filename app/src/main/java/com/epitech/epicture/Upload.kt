package com.epitech.epicture

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import com.epitech.epicture.ui.send.SendActivity

/***
 * Object containing Upload related functions
 */
object Upload {

    private const val IMAGE = 0
    private const val CAMERA = 1

    var preferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null

    /***
     * Allows the user to get an image from the android's gallery
     * @param activity
     */
    fun getImageFromGallery(activity: Activity) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(activity, Intent.createChooser(intent, "Select Picture"), IMAGE, null)
    }

    /***
     * Allows the user to get an image from the android's camera
     * @param activity
     */
    fun getImageFromCamera(activity: Activity) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.also { Intent ->
            Intent.resolveActivity(activity.packageManager).also {
                startActivityForResult(activity, Intent, CAMERA, null)
            }
        }
    }

    /***
     * Private function that starts the SendActivity for upload purposes
     * @param context
     * @param bitmap
     */
    private fun startUploadFromBitmap(context: Context, bitmap: Bitmap) {
        val filename = "temp.png"
        val fs = context.openFileOutput(filename, Context.MODE_PRIVATE)
        val intent = Intent(context, SendActivity::class.java)

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fs)

        fs?.close()
        bitmap.recycle()

        intent.putExtra("image", filename);
        startActivity(context, intent, null)
    }

    fun onActivityResult(context: Context, requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK)
            return
        when (requestCode) {
            IMAGE -> {
                val uri = data?.data
                val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                startUploadFromBitmap(context, bitmap)
            }
            CAMERA -> {
                val bitmap = data?.extras?.get("data") as Bitmap
                startUploadFromBitmap(context, bitmap)
            }
        }
    }
}