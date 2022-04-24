package com.example.moviesearch.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.moviesearch.repository.LocalDatabaseRepository
import com.example.moviesearch.room.entities.SearchHistory
import kotlinx.coroutines.launch

class LocalDatabaseViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: LocalDatabaseRepository by lazy { LocalDatabaseRepository.getInstance(application) }
    private val searchHistoryMutableLiveData by lazy { MutableLiveData<List<String>>() }
    private val isSelectFailedMutableLiveData by lazy { MutableLiveData<Boolean>() }
    private val isInsertFailedMutableLiveData by lazy { MutableLiveData<Boolean>() }
    var searchHistoryLiveData: LiveData<List<String>> = searchHistoryMutableLiveData
    var isSelectFailedLiveData: LiveData<Boolean> = isSelectFailedMutableLiveData
    var isInsertFailedLiveData: LiveData<Boolean> = isInsertFailedMutableLiveData

    // Room DB 검색 이력 호출
    fun getSearchHistory(limit: Int) {
        viewModelScope.launch {
            runCatching {
                repository.getSearchHistory(limit)

            }.onSuccess { result -> // Result 반환
                searchHistoryMutableLiveData.value = result

            }.onFailure { // Throwable 반환
                isSelectFailedMutableLiveData.value = true // 검색에 실패했을 경우
            }
        }
    }

    // Room DB 검색 이력 저장
    fun insertHistory(searchHistoryDTO: SearchHistory) {
        viewModelScope.launch {
            runCatching {
                repository.insertSearchHistory(searchHistoryDTO)

            }.onFailure {
                isInsertFailedMutableLiveData.value = true // 삽입에 실패했을 경우
            }
        }
    }
}