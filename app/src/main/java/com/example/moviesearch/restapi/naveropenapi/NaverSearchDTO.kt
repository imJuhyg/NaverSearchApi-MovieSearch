package com.example.moviesearch.restapi.naveropenapi

import com.google.gson.annotations.SerializedName

/** MovieItems
 * total: 검색 결과의 총 개수
 * start: 검색 시작 위치(default: 1)
 * display: 검색 결과 출력 건수(default: 10)
 * items: MovieDTO
 */
data class MovieItems(val total: Int, val start: Int, val display: Int, val items: List<MovieDTO>)

data class MovieDTO(
    var title: String,
    @SerializedName("link") val infoLink: String,
    @SerializedName("image") val imageLink: String,
    val userRating: String,
    val pubDate: String
)
