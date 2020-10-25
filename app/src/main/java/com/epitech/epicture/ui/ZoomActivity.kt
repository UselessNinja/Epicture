package com.epitech.epicture.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.epitech.epicture.ImgurServices
import com.epitech.epicture.R
import com.epitech.epicture.jsonmodels.Converter
import com.epitech.epicture.jsonmodels.ImgurPost
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import org.w3c.dom.Text

/***
 * Class containing everything inside the posts
 */
class ZoomActivity : AppCompatActivity() {
    companion object { val EXTRA = "ZoomActivity.EXTRA" }

    private lateinit var imageView: ImageView
    private lateinit var image: ImgurPost
    private lateinit var favoriteButton: ImageButton
    private lateinit var titleView: TextView
    private lateinit var viewView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zoom)

        image = intent.getParcelableExtra(EXTRA)
        imageView = findViewById(R.id.image_zoom)
        favoriteButton = findViewById(R.id.favorite_button)
        titleView = findViewById(R.id.title_zoom)
        viewView = findViewById(R.id.view_zoom)
    }

    override fun onStart() {
        super.onStart()
        Log.d("ZOOM", image.toString())
        Picasso.get()
            .load(image.preview)
            .placeholder(R.drawable.ic_menu_camera)
            .error(R.drawable.ic_menu_gallery)
            .fit()
            .into(imageView)
        if (image.favorite!!)
            favoriteButton.setBackgroundResource(R.drawable.ic_favorite_24px)
        else
            favoriteButton.setBackgroundResource(R.drawable.ic_favorite_border_24px)
        titleView.text = image.title ?: "Untitled"
        viewView.setText(image.totalViews.toString() + " views")
    }

    /***
     * Allows the user to favorite/unfavorite an image by pressing the heart button
     * @param view (unused)
     */
    fun favorite(view : View) {
        if (image.favorite!!)
            favoriteButton.setBackgroundResource(R.drawable.ic_favorite_border_24px)
        else
            favoriteButton.setBackgroundResource(R.drawable.ic_favorite_24px)
        ImgurServices.changeFavoriteState(this, {
            Log.d("ZOOM", "it: " + it.toString())
            if (it.asJsonObject.get("data").asString == "favorited") {
                favoriteButton.setBackgroundResource(R.drawable.ic_favorite_24px)
                runOnUiThread {
                    Toast.makeText(this, "Added to Favorites!", Toast.LENGTH_SHORT).show()
                }
            } else {
                favoriteButton.setBackgroundResource(R.drawable.ic_favorite_border_24px)
                runOnUiThread {
                    Toast.makeText(this, "Removed from Favorites!", Toast.LENGTH_SHORT).show()
                }
            }
        }, {
            e -> runOnUiThread {
                Toast.makeText(this, "couldn't favorite/unfavorite : $e", Toast.LENGTH_SHORT).show()
            }
        }, image.id!!, image.type)
    }
}