package com.example.pinterest.model.relatedcollection

import com.example.pinterest.model.homephoto.Urls

data class SinglePhoto(
    val id: String,
    val urls: Urls,
    val likes: Long,
    val related_collections: RelatedCollections,
)
