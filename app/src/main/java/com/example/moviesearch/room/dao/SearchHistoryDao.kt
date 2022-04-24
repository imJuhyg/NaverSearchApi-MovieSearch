package com.example.moviesearch.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.moviesearch.room.entities.SearchHistory

@Dao
interface SearchHistoryDAO {
    @Query("SELECT searchWord FROM SearchHistory " +
            "WHERE dateTime IN (SELECT MAX(dateTime) FROM SearchHistory GROUP BY searchWord) " +
            "ORDER BY dateTime DESC " +
            "LIMIT :limit")
    fun getSearchHistory(limit: Int): List<String>

    @Insert
    fun insertSearchHistory(searchHistoryDTO: SearchHistory)
}