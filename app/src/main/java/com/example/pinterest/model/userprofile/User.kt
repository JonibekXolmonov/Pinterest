package com.example.pinterest.model.userprofile


data class User(
    val id: String,
    val username: String,
    val name: String,
    val profile_image: ProfileImage,
    val links: Links,
    val followers_count: Long,
    val following_count: Long,
)
