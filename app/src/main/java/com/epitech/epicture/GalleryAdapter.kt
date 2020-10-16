package com.epitech.epicture

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.media.MediaMetadataRetriever
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat.startActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.epitech.epicture.jsonmodels.ImgurPost
import com.epitech.epicture.ui.ZoomActivity
import com.squareup.picasso.Picasso
import com.squareup.picasso.Request
import com.squareup.picasso.RequestHandler


class GalleryAdapter(val context: Context, val gallery: ArrayList<ImgurPost>) : RecyclerView.Adapter<GalleryAdapter._ViewHolder>() {

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

        val picasso = Picasso.Builder(context).addRequestHandler(VideoRequestHandler()).build()
            picasso.load(image.preview)
            .placeholder(R.drawable.ic_menu_camera)
            .error(R.drawable.ic_menu_gallery).fit()
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

    inner class VideoRequestHandler : RequestHandler() {

        override fun canHandleRequest(data: Request?): Boolean {
            return "mp4".equals(data?.uri?.toString()?.substringAfterLast('.',""))
        }

        override fun load(request: Request?, networkPolicy: Int): Result? {
            var bitmap : Bitmap? = null
            var bitmapOverlay : Bitmap? = null
            var metadataRetriever : MediaMetadataRetriever? = null

            try {
                metadataRetriever = MediaMetadataRetriever()
                metadataRetriever.setDataSource(request?.uri?.toString(), HashMap<String, String>())
                bitmap = metadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST)
            } catch (e : Exception) {
                Log.e("ERROR", "Exception : $e")
            } finally {
                metadataRetriever?.release()
            }

            if (bitmap != null) {
                bitmapOverlay = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmapOverlay!!)
                canvas.drawBitmap(bitmap, 0.0f, 0.0f, null)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    canvas.drawBitmap(context.resources.getDrawable(R.drawable.ic_menu_camera , context.theme).toBitmap(bitmap.width/2, bitmap.height/2), bitmap.width/4f, bitmap.height/4f, null)
                }
            }

            return Result(bitmapOverlay!!, Picasso.LoadedFrom.MEMORY)
        }
    }
}