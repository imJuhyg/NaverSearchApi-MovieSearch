package com.example.moviesearch.restapi.naversearchapi

import com.example.moviesearch.restapi.RetrofitManager

abstract class NaverSearchManager<T: Any> : RetrofitManager(NAVER_SEARCH_API_URL) {
    // T: 결과로 받을 DTO 객체 타입을 명시. ex) MovieDTO, ...
    // SearchType ex) movie.json, book.json, news.json ...

    protected val CLIENT_ID = "U3EGn6EooBj0_Kkc5iJU"
    protected val CLIENT_SECRET = "ZUVF06lS3m"

    companion object {
        private const val NAVER_SEARCH_API_URL = "https://openapi.naver.com/v1/"
    }

    /** 검색 요청 메소드
     * startIndex: 검색 시작 위치이며 기본값은 1입니다. 첫 검색은 기본값으로 제공하고, 다음 검색을 요청하고 싶은 경우 다음 검색의 시작위치를 넣으면 됩니다.
     * searchQuery: 검색을 원하는 문자열입니다.
     * onSuccess: 검색에 성공했을 때 콜백입니다. 결과가 리스트형태로 제공되고 다음 검색의 시작 위치가 제공됩니다.
     * onFailure: 요청에는 성공했지만, 에러 코드가 발생한 경우입니다. 에러 코드를 콜백합니다.
     * onError: 요청 자체를 실패한 경우입니다. 요청한 서버에 장애가 발생했거나, 개발자의 잘못된 코드가 원인일 수도 있습니다.
     */
    abstract fun searchInfo(startIndex: Int,
                            searchQuery: String,
                            onSuccess: (resultList: List<T>, nextIndex: Int) -> Unit,
                            onFailure: (errorCode: Int) -> Unit,
                            onError: (throwable: Throwable) -> Unit)


    /** 다음 검색의 시작 위치를 구하는 메소드
     * 요청의 결과로 받은 start, display, total 값을 인자로 넣으면 다음 검색의 시작 위치를 반환합니다.
     */
    fun getNextIndex(start: Int, display: Int, total: Int): Int = if(start + display <= total) {
        start+display
    } else -1
}