package com.example.moviesearch.restapi.naversearchapi

import com.example.moviesearch.restapi.RetrofitManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

abstract class NaverSearchManager(val searchType: String) : RetrofitManager(NAVER_SEARCH_API_URL) {
    // SearchType ex) movie.json, book.json, news.json ...
    // private val movieSearchApi = getRetrofit().create(NaverSearchApi::class.java)

    companion object {
        private const val NAVER_SEARCH_API_URL = "https://openapi.naver.com/v1/"
        private const val CLIENT_ID = "U3EGn6EooBj0_Kkc5iJU"
        private const val CLIENT_SECRET = "ZUVF06lS3m"
    }

    /** 검색 요청 메소드
     * startIndex: 검색 시작 위치이며 기본값은 1입니다. 첫 검색은 기본값으로 제공하고, 다음 검색을 요청하고 싶은 경우 다음 검색의 시작위치를 넣으면 됩니다.
     * searchQuery: 검색을 원하는 문자열입니다.
     * onSuccess: 검색에 성공했을 때 콜백입니다. 결과가 리스트형태로 제공되고 다음 검색의 시작 위치가 제공됩니다.
     * onFailure: 요청에는 성공했지만, 에러 코드가 발생한 경우입니다. 에러 코드를 콜백합니다.
     * onError: 요청 자체를 실패한 경우입니다. 요청한 서버에 장애가 발생했거나, 개발자의 잘못된 코드가 원인일 수도 있습니다.
     */

    abstract fun searchInfo(startIndex: Int = 1,
                            searchQuery: String,
                            onSuccess: (resultList: List<Any>, nextIndex: Int) -> Unit,
                            onFailure: (errorCode: Int) -> Unit,
                            onError: (throwable: Throwable) -> Unit)

    /** 다음 검색의 시작 위치를 구하는 메소드
     * 요청의 결과로 받은 start, display, total 값을 인자로 넣으면 다음 검색의 시작 위치를 반환합니다.
     */

    fun getNextIndex(start: Int, display: Int, total: Int) {
        val nextIndex = if(start + display <= total) {
            start+display
        } else -1
    }


    /*

    /**
     * 영화 검색 API:
     * startIndex = 검색 시작 위치, searchWord = 검색어
     * success = 성공시 콜백, failure = 실패시 콜백
     */
    fun getMovieInfo(startIndex: Int = 1, searchWord: String?,
                     onSuccess: (movieList: List<MovieDTO>, nextIndex: Int) -> Unit,
                     onFailure: (errorCode: Int) -> Unit,
                     onError: (throwable: Throwable) -> Unit) {

        // 호출할 URL 구성
        val call = movieSearchApi.searchData(
            CLIENT_ID,
            CLIENT_SECRET,
            searchType,
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

     */
}