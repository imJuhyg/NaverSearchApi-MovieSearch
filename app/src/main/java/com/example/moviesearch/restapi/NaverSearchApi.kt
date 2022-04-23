package com.example.moviesearch.restapi

import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.*

interface NaverSearchApi {
    @GET("search/{searchType}")
    fun searchData(
        @Header("X-Naver-Client-Id") clientId: String,
        @Header("X-Naver-Client-Secret") clientSecret: String,
        @Path("searchType") searchType: String,
        @Query("query") searchQuery: String?,
        @Query("start") startIndex: Int
    ): Call<MovieItems>

}