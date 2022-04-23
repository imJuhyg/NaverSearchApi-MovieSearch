package com.example.moviesearch.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.moviesearch.room.entities.SearchHistory

@Dao
interface SearchHistoryDao {
    @Query("SELECT searchName FROM SearchHistory GROUP BY searchName ORDER BY time DESC LIMIT :limit")
    fun getSearchHistory(limit: Int): List<String>

    @Insert
    fun insertSearchHistory(searchHistoryDto: SearchHistory)
}