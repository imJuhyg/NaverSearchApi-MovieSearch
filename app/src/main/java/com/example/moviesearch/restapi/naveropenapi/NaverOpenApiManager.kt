package com.example.moviesearch.restapi.naveropenapi

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
                     onSuccess: (movieList: List<MovieDTO>, nextIndex: Int) -> Unit,
                     onFailure: (errorCode: Int) -> Unit,
                     onError: (throwable: Throwable) -> Unit) {

        val call = movieSearchApi.searchData(
            CLIENT_ID,
            CLIENT_SECRET,
            "movie.json",
            searchWord,
            startIndex
        ) // url ex) https://openapi.naver.com/v1/movie.json?query={searchWord}&startIndex={startIndex}

        call.enqueue(object: Callback<MovieItems> { // 비동기 호출
            override fun onResponse(call: Call<MovieItems>, response: Response<MovieItems>) {
                when {
                    response.isSuccessful -> {
                        val results = response.body()!!

                        // start+display = 다음 인덱스
                        val nextIndex = if(results.start+results.display <= results.total) {
                            results.start+results.display
                        } else -1 // 다음 인덱스가 없는 경우

                        for(movieDTO in results.items) {
                            movieDTO.title = movieDTO.title.replace("&amp;", "&") // '&amp;'로 출력되는 결과 -> '&'으로 변경
                            movieDTO.title = movieDTO.title.replace("(<b>|</b>)".toRegex(), "") // 불필요한 태그 삭제
                        }
                        onSuccess(results.items, nextIndex) // Callback List<MovieDTO>, nextIndex
                    }

                    // 응답에는 성공했지만 에러 코드인 경우
                    response.code() == 400 -> { // 잘못된 검색어 입력
                        onFailure(400)
                    }

                    response.code() == 500 -> { // 네이버 Open API 서버 에러
                        onFailure(500)
                    }
                }
            }

            override fun onFailure(call: Call<MovieItems>, throwable: Throwable) {
                onError(throwable)
            }
        })
    }
}