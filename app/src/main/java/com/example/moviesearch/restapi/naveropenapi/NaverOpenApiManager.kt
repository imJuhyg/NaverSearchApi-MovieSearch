package com.example.moviesearch.restapi.naveropenapi

import android.util.Log
import com.example.moviesearch.restapi.RetrofitManager
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

    /**
     * 영화 검색 API:
     * startIndex = 검색 시작 위치, searchWord = 검색어
     * success = 성공시 콜백, failure = 실패시 콜백
     */
    fun getMovieInfo(startIndex: Int = 1, searchWord: String?,
                     success: (List<MovieDTO>, nextIndex: Int) -> Unit,
                     failure: (Throwable) -> Unit) {

        val call = movieSearchApi.searchData(
            CLIENT_ID,
            CLIENT_SECRET,
            "movie.json",
            searchWord,
            startIndex
        ) // url ex) https://openapi.naver.com/v1/movie.json?query={searchWord}&startIndex={startIndex}

        call.enqueue(object: Callback<MovieItems> {
            override fun onResponse(call: Call<MovieItems>, response: Response<MovieItems>) {
                if(response.isSuccessful) {
                    val results = response.body()!!

                    // start+display = 다음 인덱스
                    val nextIndex = if(results.start+results.display <= results.total) {
                        results.start+results.display
                    } else {
                        -1 // 다음 인덱스가 없는 경우
                    }

                    // 불필요한 태그 삭제
                    for(movieDTO in results.items) {
                        // TODO 영화 제목에 '&' 가 들어가면 &amp;로 출력됨
                        movieDTO.title = movieDTO.title.replace("(<b>|</b>)".toRegex(), "")
                    }

                    success(results.items, nextIndex) // Callback List<MovieDTO>, nextIndex
                }
            }

            override fun onFailure(call: Call<MovieItems>, t: Throwable) {
                failure(t)
            }
        })
    }
}