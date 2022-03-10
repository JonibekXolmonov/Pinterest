package com.example.pinterest.model.search

data class ResponseSearch(
    val results:List<Result>,
    val likes:Long
)