package com.epitech.epicture.jsonmodels

/***
 * Json model for Imgurs' Response Model
 */
data class ImgurModels<T> (
    val data : T,
    val success: Boolean,
    val status : Int
)