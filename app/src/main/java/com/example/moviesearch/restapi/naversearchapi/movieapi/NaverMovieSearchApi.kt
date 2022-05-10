package com.example.moviesearch.restapi.naversearchapi.movieapi

import com.example.moviesearch.restapi.naversearchapi.MovieItems
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface NaverMovieSearchApi {
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