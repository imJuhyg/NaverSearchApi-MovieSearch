package com.example.moviesearch.restapi.naveropenapi

import retrofit2.Call
import retrofit2.http.*

interface NaverSearchApi {
    /**
     * searchType: 검색 종류 ex) movie.json, news.json, book.json ...
     * searchQuery: 검색어
     * startIndex: 검색의 시작 위치 (default: 1)
     */
    @GET("search/{searchType}")
    fun searchData(
        @Header("X-Naver-Client-Id") clientId: String,
        @Header("X-Naver-Client-Secret") clientSecret: String,
        @Path("searchType") searchType: String,
        @Query("query") searchQuery: String?,
        @Query("start") startIndex: Int
    ): Call<MovieItems>
}