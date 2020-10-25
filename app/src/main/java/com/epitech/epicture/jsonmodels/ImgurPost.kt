package com.epitech.epicture.jsonmodels

import android.os.Parcelable
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonElement
import kotlinx.android.parcel.Parcelize

/***
 * Json model for an unified data format for an Imgur Post
 */
@Parcelize
data class ImgurPost (
    val id: String?,
    val title: String?,
    val upvotes: Int?,
    val downvotes: Int?,
    val vote: String?,
    val totalViews: Int?,
    val favorite: Boolean?,
    val preview: String?,
    val video: String?,
    val element: String,
    val type: Type
) : Parcelable

/***
 * enum for the filters
 */
enum class FilterType {
    NONE,
    IMAGE,
    VIDEO
}

/***
 * enum for the type of imgur formats
 */
enum class Type {
    ImagePost,
    Album,
    Image
}

/***
 * Class containing functions to convert jsonElements and Images into ImgurPosts
 */
class Converter {
    companion object {
        /***
         * function for converting json responses for imgur into an usuable generic format
         * @param element JsonElement to be converted
         * @return ImgurPost
         */
        fun jsonElementToImgurPost(element: JsonElement): ImgurPost {
            if (element.asJsonObject.has("images")) {
                val content = Gson().fromJson<Album>(element, Album::class.java)
                var preview = content.images?.get(0)?.link
                if (content.cover != null)
                    preview = "https://i.imgur.com/" + content.cover + ".jpg"
                return ImgurPost(
                    content.id,
                    content.title,
                    content.ups,
                    content.downs,
                    content.vote,
                    content.views,
                    content.favorite,
                    preview,
                    content.images?.get(0)?.mp4,
                    element.toString(),
                    Type.Album
                )
            } else {
                val content = Gson().fromJson<ImagePost>(element, ImagePost::class.java)
                return ImgurPost(
                    content.id,
                    content.title,
                    content.ups,
                    content.downs,
                    content.vote,
                    content.views,
                    content.favorite,
                    content.link,
                    content.mp4,
                    element.toString(),
                    Type.ImagePost
                )
            }
        }
        /***
         * function for converting a single image into an usuable generic format
         * @param element Image to be converted
         * @return ImgurPost
         */
        fun imageToImgurPost(element: Image): ImgurPost {
            return ImgurPost(
                element.id,
                element.title,
                null,
                null,
                element.vote,
                element.views,
                element.favorite,
                element.link,
                element.mp4,
                element.toString(),
                Type.Image
            )
        }
    }
}



