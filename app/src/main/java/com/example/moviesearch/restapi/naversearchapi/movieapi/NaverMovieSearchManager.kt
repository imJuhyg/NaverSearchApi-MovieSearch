package com.example.moviesearch.restapi.naversearchapi.movieapi

import com.example.moviesearch.restapi.naversearchapi.NaverSearchManager

class NaverMovieSearchManager(searchType: String) : NaverSearchManager(searchType) {
    override fun searchInfo(
        startIndex: Int,
        searchQuery: String,
        onSuccess: (resultList: List<Any>, nextIndex: Int) -> Unit,
        onFailure: (errorCode: Int) -> Unit,
        onError: (throwable: Throwable) -> Unit
    ) {
        TODO("Not yet implemented")
    }
}