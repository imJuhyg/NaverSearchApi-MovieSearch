package com.example.moviesearch.searchapi

import com.google.gson.annotations.SerializedName
import java.util.*

data class MovieItems(val total: Int, val start: Int, val display: Int, val items: List<MovieDTO>)

data class MovieDTO(
    var title: String,
    @SerializedName("link") val movieLink: String,
    @SerializedName("image") val imageLink: String,
    val userRating: String,
    val pubDate: String
)
