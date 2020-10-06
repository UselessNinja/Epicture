package com.epitech.epicture

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.epitech.epicture.jsonmodels.Image
import com.epitech.epicture.ui.ZoomActivity
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

class GalleryAdapter(val context: Context, val gallery: ArrayList<Image>) : RecyclerView.Adapter<GalleryAdapter._ViewHolder>() {

    @SuppressLint("ResourceType")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): _ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val imageView = inflater.inflate(R.xml.item_image, parent, false)
        return _ViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: _ViewHolder, position: Int) {
        val image = gallery[position]
        val imageView = holder.galleryImageView

        //TODO Filter albums from images

        Picasso.get()
            .load(image.link!!.toByteArray(Charsets.UTF_8).toString(Charsets.UTF_8))
            .placeholder(R.drawable.ic_menu_camera)
            .error(R.drawable.ic_menu_gallery)
            .fit()
            .priority(Picasso.Priority.HIGH)
            .into(imageView)
    }

    override fun getItemCount(): Int {
        return gallery.size
    }

    inner class _ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var galleryImageView: ImageView = itemView.findViewById(R.id.image)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val galleryImage = gallery[position]
                val intent = Intent(context, ZoomActivity::class.java).apply {
                    putExtra(ZoomActivity.EXTRA, galleryImage)
                }
                startActivity(context, intent, null)
            }
        }
    }

}