package com.example.moviesearch.repository

import android.app.Application
import androidx.room.Room
import com.example.moviesearch.room.databases.LocalDatabase
import com.example.moviesearch.room.entities.SearchHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalDatabaseRepository private constructor() {
    companion object {
        private var instance: LocalDatabaseRepository? = null
        private lateinit var localDatabase: LocalDatabase

        fun getInstance(application: Application): LocalDatabaseRepository =
            instance ?: synchronized(this) {
                instance ?: LocalDatabaseRepository().also {
                    instance = it
                    localDatabase = Room.databaseBuilder(application, LocalDatabase::class.java, "LocalDB").build()
                }
            }
    }

    // 대상 Entity: SearchHistory
    // 기능: limit 만큼의 최신 검색 이력 가져오기
    suspend fun getSearchHistory(limit: Int): List<String> = withContext(Dispatchers.IO) {
        localDatabase.searchHistoryDAO().getSearchHistory(limit)
    }

    // 대상 Entity: SearchHistory
    // 기능: System time, searchName 정보를 담아 테이블에 저장
    suspend fun insertSearchHistory(searchHistoryDTO: SearchHistory) = withContext(Dispatchers.IO) {
        localDatabase.searchHistoryDAO().insertSearchHistory(searchHistoryDTO)
    }
}