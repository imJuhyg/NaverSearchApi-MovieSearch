package com.example.moviesearch.restapi

import android.util.Log
import com.google.gson.JsonElement
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

abstract class RetrofitManager(private val baseUrl: String) {
    fun getRetrofit(): Retrofit {
        val client = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor(object: HttpLoggingInterceptor.Logger{
            override fun log(message: String) {
                Log.d("Logging-Interceptor", message)
            }
        })
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS)
        client.addInterceptor(loggingInterceptor)
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client.build())
            .build()
    }
}