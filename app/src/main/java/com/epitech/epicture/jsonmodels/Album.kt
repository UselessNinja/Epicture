package com.epitech.epicture.jsonmodels

/***
 * Json model for Imgurs' Albums
 */
data class Album (
    val id : String?,
    var link: String?,
    val views: Int?,
    val ups: Int?,
    val downs: Int?,
    val account_url: String?,
    val title: String?,
    val description: String?,
    var selected: Boolean = false,
    var favorite: Boolean?,
    val vote: String?,
    var images: ArrayList<Image>?,
    val cover: String?
)