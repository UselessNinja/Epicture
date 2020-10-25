package com.epitech.epicture.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaMetadataRetriever
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.epitech.epicture.R
import com.epitech.epicture.jsonmodels.ImgurPost
import com.epitech.epicture.ui.ZoomActivity
import com.squareup.picasso.Picasso
import com.squareup.picasso.Request
import com.squareup.picasso.RequestHandler

/***
 * Adapter for the RecyclerView of the Home Fragment
 */
class SearchAdapter(val context: Context, val gallery: ArrayList<ImgurPost>) : RecyclerView.Adapter<SearchAdapter._ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): _ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val imageView = inflater.inflate(R.layout.item_image, parent, false)
        return _ViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: _ViewHolder, position: Int) {
        val image = gallery[position]
        val imageView = holder.galleryImageView
        val textView = holder.titleTextView
        textView.text = image.title ?: "Untitled"

        val picasso = Picasso.Builder(context).addRequestHandler(VideoRequestHandler()).build()
            picasso.load(image.preview)
            .placeholder(R.drawable.ic_loading)
            .error(R.drawable.ic_error_24dp).fit()
            .priority(Picasso.Priority.HIGH)
            .into(imageView)
    }

    override fun getItemCount(): Int {
        return gallery.size
    }

    inner class _ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var galleryImageView: ImageView = itemView.findViewById(R.id.image)
        var titleTextView: TextView = itemView.findViewById(R.id.title)

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

    /***
     * Handler for making custom thumbnails for .mp4 images/albums
     */
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
                    canvas.drawBitmap(context.resources.getDrawable(R.drawable.ic_play_arrow, context.theme).toBitmap(bitmap.width/2, bitmap.height/2), bitmap.width/4f, bitmap.height/4f, null)
                }
            }

            return Result(bitmapOverlay!!, Picasso.LoadedFrom.MEMORY)
        }
    }
}