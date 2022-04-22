package com.example.moviesearch.searchapi

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NaverOpenApiManager : RetrofitManager(NAVER_OPEN_API_URL) {
    private val movieSearchApi = getRetrofit().create(NaverSearchApi::class.java)

    companion object {
        private const val NAVER_OPEN_API_URL = "https://openapi.naver.com/v1/"
        private const val CLIENT_ID = "U3EGn6EooBj0_Kkc5iJU"
        private const val CLIENT_SECRET = "ZUVF06lS3m"
    }

    fun getMovieInfo(startIndex: Int = 1, partTitle: String,
                     success: (List<MovieDTO>, nextPage: Int) -> Unit,
                     failure: (Throwable) -> Unit) {

        val call = movieSearchApi.searchData(
            CLIENT_ID,
            CLIENT_SECRET,
            "movie.json",
            partTitle,
            startIndex
        )

        call.enqueue(object: Callback<MovieItems> {
            override fun onResponse(call: Call<MovieItems>, response: Response<MovieItems>) {
                if(response.isSuccessful) {
                    val results = response.body()!!

                    // nextPage: 있으면 다음 page 없으면 -1
                    val nextPage = if(results.start+results.display <= results.total) {
                        results.start+results.display
                    } else {
                        -1
                    }

                    // 불필요한 태그 삭제
                    for(item in results.items) {
                        item.title = item.title.replace("(<b>|</b>)".toRegex(), "")
                    }

                    success(results.items, nextPage) // List<MovieDTO>, nextPage
                }
            }

            override fun onFailure(call: Call<MovieItems>, t: Throwable) {
                failure(t)
            }
        })
    }
}