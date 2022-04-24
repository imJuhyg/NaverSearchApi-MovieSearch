package com.example.moviesearch.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class SearchHistory(
    @PrimaryKey(autoGenerate = true) var id: Int,
    @ColumnInfo var dateTime: Date,
    @ColumnInfo var searchWord: String
) {
    constructor(dateTime: Date, searchWord: String) : this(0, dateTime, searchWord)
}