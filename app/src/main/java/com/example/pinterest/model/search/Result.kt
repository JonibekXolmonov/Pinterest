package com.example.pinterest.model.search

import com.example.pinterest.model.homephoto.Urls

data class Result(
    val id:String,
    val description:String,
    val urls: Urls
)
