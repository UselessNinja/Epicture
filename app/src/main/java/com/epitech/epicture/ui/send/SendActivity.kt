package com.epitech.epicture.ui.send

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.epitech.epicture.ImgurServices
import com.epitech.epicture.R
import kotlinx.android.synthetic.main.activity_send.*

class SendActivity : AppCompatActivity() {

    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send)
        val filename = intent.getStringExtra("image")
        val stream = this.openFileInput(filename)
        bitmap = BitmapFactory.decodeStream(stream)
        imageView.setImageBitmap(bitmap)
        stream.close()
        button.setOnClickListener {
            if (descriptionText.text.isEmpty() || titleText.text.isEmpty())
                return@setOnClickListener Toast.makeText(this, "Field are missing", Toast.LENGTH_SHORT).show()
            val title = titleText.text.toString()
            val description = descriptionText.text.toString()
            ImgurServices.upload(this, {_ ->
            }, {_ ->
            },
                "upload", title, description, bitmap
            )
            super.onBackPressed()
        }
    }
}