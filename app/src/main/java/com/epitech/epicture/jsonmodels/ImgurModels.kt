package com.epitech.epicture.jsonmodels

data class ImgurModels<T> (
    val data : T,
    val success: Boolean,
    val status : Int
)