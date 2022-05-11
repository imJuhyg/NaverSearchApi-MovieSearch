package com.example.moviesearch.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moviesearch.restapi.naversearchapi.movieapi.MovieDTO
import com.example.moviesearch.restapi.naversearchapi.movieapi.NaverMovieSearchManager

class SearchApiViewModel : ViewModel() {
    private val naverMovieSearchManager by lazy { NaverMovieSearchManager() }
    private val searchResultListLiveData: MutableLiveData<List<MovieDTO>> by lazy { MutableLiveData() }
    private val nextIndexLiveData: MutableLiveData<Int> by lazy { MutableLiveData() }
    private val errorCodeLiveData: MutableLiveData<Int> by lazy { MutableLiveData() }
    private val throwableLiveData: MutableLiveData<Throwable> by lazy { MutableLiveData() }
    val searchResult: LiveData<List<MovieDTO>> = this.searchResultListLiveData
    val nextIndex: LiveData<Int> = nextIndexLiveData
    val errorCode: LiveData<Int> = errorCodeLiveData
    val throwable: LiveData<Throwable> = throwableLiveData

    /**
     * NaverSearchManager 를 상속받는 검색 API 를 만들고,
     * SearchApiViewModel 에서 API 호출 메소드를 만들기만 하면 View 로 데이터를 전달할 수 있습니다.
     * 아래 searchMovie 메소드는 영화 검색 API 를 호출하는 메소드입니다.
     */

    fun searchMovie(startIndex: Int = 1, searchQuery: String) {
        naverMovieSearchManager.searchInfo(startIndex, searchQuery,
            onSuccess = { resultList, nextIndex ->
                searchResultListLiveData.value = resultList
                nextIndexLiveData.value = nextIndex

            },
            onFailure = { errorCode ->
                errorCodeLiveData.value = errorCode
            },
            onError = { throwable ->
                throwableLiveData.value = throwable
            }
        )
    }

}