package com.epitech.epicture.jsonmodels

data class Image (
    val id : String?,
    val title : String?,
    val description : String?,
    val datetime : Int?,
    val type : String?,
    val animated : Boolean?,
    val width :	Int?,
    val height : Int?,
    val size : Int?,
    val views : Int?,
    val bandwidth :	Int?,
    val deletehash : String?,
    val name : String?,
    val section : String?,
    val link : String?,
    val gifv : String?,
    val mp4 : String?,
    val mp4_size : Int?,
    val looping : Boolean?,
    val favorite : Boolean?,
    val nsfw : Boolean?,
    val vote : String?,
    val in_gallery : Boolean?
)