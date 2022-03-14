package com.example.pinterest.networking.services

import com.example.pinterest.model.homephoto.HomePhotoItem
import com.example.pinterest.model.relatedcollection.SinglePhoto
import com.example.pinterest.model.search.ResponseSearch
import com.example.pinterest.model.topic.Topic
import com.example.pinterest.model.userprofile.User
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
    fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Call<ResponseSearch>

    @GET("users/{username}")
    fun getUser(@Path("username") username: String): Call<User>

    @GET("photos/{id}")
    fun getImageToRelated(
        @Path("id") id: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Call<SinglePhoto>

    @GET("topics")
    fun getTopics(): Call<List<Topic>>

    @GET("topics/{id}/photos")
    fun getTopicPhotos(
        @Path("id") id: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Call<List<HomePhotoItem>>
}