package com.example.moviesearch.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class SearchHistory(
    @PrimaryKey(autoGenerate = true) var id: Int,
    @ColumnInfo var time: Date,
    @ColumnInfo var searchName: String
) {
    constructor(time: Date, searchName: String) : this(0, time, searchName)
}