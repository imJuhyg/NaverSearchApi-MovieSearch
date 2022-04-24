package com.example.moviesearch.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.moviesearch.repository.LocalDatabaseRepository
import com.example.moviesearch.room.entities.SearchHistory
import kotlinx.coroutines.launch

class LocalDatabaseViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: LocalDatabaseRepository by lazy { LocalDatabaseRepository.getInstance(application) }
    val searchHistoryLiveData by lazy { MutableLiveData<List<String>>() }
    val isInserted by lazy { MutableLiveData<Boolean>() }

    fun getSearchHistory(limit: Int) {
        viewModelScope.launch {
            runCatching {
                repository.getSearchHistory(limit)

            }.onSuccess { result -> // Result 반환
                searchHistoryLiveData.value = result

            }.onFailure { // Throwable 반환
                // TODO 에러 처리
            }

        }
    }

    fun insertHistory(searchHistoryDto: SearchHistory) {
        viewModelScope.launch {
            runCatching {
                repository.insertSearchHistory(searchHistoryDto)

            }.onSuccess {
                isInserted.value = true

            }.onFailure {
                it.printStackTrace()
                Log.d("실패", "실패!")
            }
        }
    }
}