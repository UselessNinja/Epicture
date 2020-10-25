package com.epitech.epicture

import com.epitech.epicture.jsonmodels.Converter
import com.epitech.epicture.jsonmodels.Image
import com.epitech.epicture.jsonmodels.Type
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConverterUnitTest {

    @Test
    fun jsonElementToImgurPostTest() {
        val json = """
            {
                "id": "nice",
                "title" : "generic_title",
                "views" : "1337",
                "ups" : "420",
                "downs" : "-69",
                "images" : [
                    {
                        "link" : "link",
                        "mp4" : "video_link"
                    }
                ],
                "favorite" : true,
                "vote": "up",
            }
        """

        val response = JsonParser().parse(json)
        val post = Converter.jsonElementToImgurPost(response)
        assertEquals("invalid id", post.id, "nice")
        assertEquals("invalid title", post.title, "generic_title")
        assertEquals("invalid view", post.totalViews, 1337)
        assertEquals("invalid upvotes", post.upvotes, 420)
        assertEquals("invalid downvotes", post.downvotes, -69)
        assertEquals("invalid preview", post.preview, "link")
        assertEquals("invalid video", post.video, "video_link")
        assertEquals("invalid favorite", post.favorite, true)
        assertEquals("invalid vote", post.vote, "up")
        assertEquals("invalid type", post.type, Type.Album)
        assertEquals("invalid element", post.element, response)
    }
}
