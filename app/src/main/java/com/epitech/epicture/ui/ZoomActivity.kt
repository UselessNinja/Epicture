package com.epitech.epicture.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.epitech.epicture.R
import com.epitech.epicture.jsonmodels.ImgurPost
import com.squareup.picasso.Picasso

class ZoomActivity : AppCompatActivity() {
    companion object { val EXTRA = "ZoomActivity.EXTRA" }

    private lateinit var imageView: ImageView
    private lateinit var image: ImgurPost

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.xml.item_image)

        image = intent.getParcelableExtra(EXTRA)
        imageView = findViewById(R.id.image)
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
    }
}