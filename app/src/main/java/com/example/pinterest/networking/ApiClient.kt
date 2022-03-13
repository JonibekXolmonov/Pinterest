package com.example.pinterest.networking

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient(var context: Context) {

    var BASE_URl = "https://api.unsplash.com/"

    private val client = getClient()
    private val retrofit = getRetrofit(client)


    private fun getRetrofit(client: OkHttpClient): Retrofit {

        val chuckInterceptor = ChuckerInterceptor(context)
        val okHttpClient = OkHttpClient
            .Builder()
            .addInterceptor(chuckInterceptor)
            .build()

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(BASE_URl)
            .client(client)
            .build()
    }

    fun <T> createService(service: Class<T>): T {
        return retrofit.create(service)
    }

    private fun getClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .addInterceptor(Interceptor { chain ->
            val builder = chain.request().newBuilder()
            builder.header("Authorization", "Client-ID 6nlmANUApAAm_Kqer-xedtHQ61JRnzuZD3AmBaHhjoQ")
            chain.proceed(builder.build())
        })
        .build()

    fun <T> createServiceWithAuth(service: Class<T>?): T {
        val newClient =
            client.newBuilder().addInterceptor(Interceptor { chain ->
                var request = chain.request()
                val builder = request.newBuilder()
                builder.addHeader(
                    "Authorization",
                    "Client-ID 6nlmANUApAAm_Kqer-xedtHQ61JRnzuZD3AmBaHhjoQ"
                )
                request = builder.build()
                chain.proceed(request)
            }).build()
        val newRetrofit = retrofit.newBuilder().client(newClient).build()
        return newRetrofit.create(service!!)
    }
}