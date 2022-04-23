package com.example.moviesearch.room.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.moviesearch.room.Converters
import com.example.moviesearch.room.dao.SearchHistoryDao
import com.example.moviesearch.room.entities.SearchHistory

@Database(
    version = 1,
    entities = [SearchHistory::class]
)
@TypeConverters(Converters::class)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun searchHistoryDao(): SearchHistoryDao
}