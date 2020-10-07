package com.epitech.epicture.jsonmodels

import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.JsonElement
import kotlinx.android.parcel.Parcelize

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

enum class Type {
    ImagePost,
    Album
}

class Converter {
    companion object {
        fun jsonElementToImgurPost(element: JsonElement): ImgurPost {
            if (element.asJsonObject.has("images")) {
                val content = Gson().fromJson<Album>(element, Album::class.java)
                return ImgurPost(
                    content.id,
                    content.title,
                    content.ups,
                    content.downs,
                    content.vote,
                    content.views,
                    content.favorite,
                    content.images?.get(0)?.link,
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
    }
}



