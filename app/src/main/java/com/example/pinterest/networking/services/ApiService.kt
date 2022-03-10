package com.example.pinterest.networking.services

import com.example.pinterest.model.homephoto.HomePhotoItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

@JvmSuppressWildcards
interface ApiService {

    @GET("photos?")
    fun getPhotos(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Call<List<HomePhotoItem>>

    @GET("photos/{id}")
    fun getSelectedPhoto(@Path("id") id: String): Call<HomePhotoItem>

    @GET("search/photos?")
    fun searchPhotos(@Query("query") query: String)

}