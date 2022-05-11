package com.example.moviesearch.restapi.naversearchapi.movieapi

import com.example.moviesearch.restapi.naversearchapi.NaverSearchManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NaverMovieSearchManager : NaverSearchManager<MovieDTO>() {
    private val movieSearchApi = getRetrofit().create(NaverMovieSearchApi::class.java)

    // 영화 검색 API
    override fun searchInfo(
        startIndex: Int,
        searchQuery: String,
        onSuccess: (resultList: List<MovieDTO>, nextIndex: Int) -> Unit,
        onFailure: (errorCode: Int) -> Unit,
        onError: (throwable: Throwable) -> Unit
    ) {

        // 호출할 URL 구성
        val call = movieSearchApi.searchData(
            CLIENT_ID,
            CLIENT_SECRET,
            "movie.json",
            searchQuery,
            startIndex
        ) // url ex) https://openapi.naver.com/v1/movie.json?query={searchWord}&startIndex={startIndex}

        call.enqueue(object: Callback<MovieItems> { // 비동기 호출
            override fun onResponse(call: Call<MovieItems>, response: Response<MovieItems>) {
                when {
                    response.isSuccessful -> {
                        val results = response.body()!!

                        // 다음 인덱스 구하기
                        val nextIndex = getNextIndex(results.start, results.display, results.total)

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