package com.example.pinterest.model.homephoto



data class HomePhotoItem(
    val alt_description: Any,
    val blur_hash: String?,
    val categories: List<Any>,
    val color: String?,
    val description: String?,
    val id: String,
    val urls: Urls,
    val links: Links,
    val user: User,
)