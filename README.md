# NaverSearchApi-MovieSearch-Sample
## 목차
[프로젝트 개요](#프로젝트-개요)  
[아키텍쳐](#아키텍쳐)  
[기술 스택](#기술-스택)  
[기타 고려 사항](#기타-고려-사항)  
[API Version Test](#api-version-test)

---
## 프로젝트 개요
* 네이버 검색 API를 이용하여 영화 검색 및 검색 이력 저장을 구현한 애플리케이션입니다.
* REST API 활용 외에도 Room Database를 이용한 검색 이력 저장에 대한 내용도 포함되어 있습니다.
* 이 프로젝트를 제작하기 위해서 [REST API 개념 및 Retrofit 실습](https://github.com/imJuhyg/restapi-study)에 대한 내용도 정리했습니다.
---
## 아키텍쳐
### MVVM  
<img src="./readme_resource/architecture.png"/>

---

## 기술 스택
### Retrofit - 네이버 영화 검색 API 호출
### Glide - 영화 썸네일 이미지 로딩
### Room Database - 최근 검색 이력 저장을 위한 로컬 데이터베이스
  
---
## 주요 구현 기능
### 재사용성을 고려한 클래스 확장
* Open API를 사용하기 위한 레트로핏 객체를 추상 클래스로 정의하여 여러 클래스에서 상속 받을 수 있도록 정의했습니다.
* NaverSearchManager는 네이버 검색 API의 모든 검색 타입을 사용할 수 있도록 확장성을 높인 추상 클래스입니다.
* Naver 검색 API만을 위한 추상 클래스이며, 다음의 기능을 수행할 수 있습니다.  
```
1. CLIENT_ID 및 CLIENT_SECRET 값을 정의할 수 있습니다.
2. 제네릭으로 구현된 추상 클래스입니다. 제네릭 타입 T를 통해 여러가지 DTO타입을 외부에서 지정할 수 있습니다.
   ex) MovieDTO, NewsDTO, BookDTO, ...
3. 검색을 요청하는 메소드는 추상 메소드로 구현되어 외부에서 오버라이딩할 수 있습니다.
4. 다음 검색의 시작 위치를 반환하는 일반 메소드를 사용할 수 있습니다.
```
  
#### RetrofitManager
```kotlin
abstract class RetrofitManager(private val baseUrl: String) {
  fun getRetrofit(): Retrofit {
    return Retrofit.Builder()
      .baseUrl(baseUrl)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
  }
}
```
  
#### NaverSearchManager
```kotlin
abstract class NaverSearchManager<T: Any> : RetrofitManager(NAVER_SEARCH_API_URL) {
  // T: 결과로 받을 DTO 객체 타입을 명시. ex) MovieDTO, ...

  protected val CLIENT_ID = "U3EGn6EooBj0_Kkc5iJU"
  protected val CLIENT_SECRET = "ZUVF06lS3m"

  companion object {
    private const val NAVER_SEARCH_API_URL = "https://openapi.naver.com/v1/"
  }

  /** 검색 요청 메소드
    * startIndex: 검색 시작 위치이며 기본값은 1입니다. 첫 검색은 기본값으로 제공하고, 
    * 다음 검색을 요청하고 싶은 경우 다음 검색의 시작위치를 넣으면 됩니다.
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
```

























### Retrofit - 네이버 영화 검색 API 호출
<b>추가 고려 사항</b>  
* 네이버 검색 API를 한번 호출 했을 때 출력 결과 건수는 10건(Default)이므로 다음 페이지 출력을 위해 '검색의 시작 위치+검색 결과 출력 건수(start+display)=다음 검색의 시작위치'를 결과 리스트와 함께 콜백합니다. 다음 검색의 시작 위치를 알고 있으므로 리사이클러뷰가 끝까지 스크롤되었을 때 다음 검색의 시작위치부터 10건의 검색 결과를 호출할 수 있습니다.

* 잘못된 검색 결과(400) 또는 네이버 Open API 서버 에러(500)시 onFailure를 콜백하고 Toast로 사용자에게 알립니다.

* 사용자의 디바이스가 네트워크를 사용할 수 없을 경우 onError를 콜백하고 Toast로 사용자에게 알립니다.

* 검색 결과의 영화 제목에서 검색어와 일치하는 부분은 ```<b></b>``` 태그로 감싸져 있기 때문에 replace 메소드를 통해 태그를 삭제한 값으로 이름을 다시 저장합니다.

&nbsp;
### Glide - 영화 썸네일 이미지 로딩
<b>추가 고려 사항</b>  
* 검색 API의 결과에 영화 Link Url은 존재하지 않을 수 있으므로 Glide.error()를 통해 대체 이미지를 표시합니다.

&nbsp;
### Room - 최근 검색 이력 저장을 위한 로컬 데이터베이스
### 테이블 설계  
<b>SearchHistory</b>
|Field|Type|Null|Pri|Ex|
|---|---|---|---|---|
|id|Int|NotNull|Pri|순번(auto increment)|
|dateTime|Date|NotNull||시간 기준 내림차순 정렬을 위한 필드|
|searchWord|String|NotNull||검색어|

### 쿼리
* dateTime 기준 내림차순 정렬/ limit으로 검색 결과 개수 제한
* Auto Increment를 통한 내림차순 정렬도 가능하나, 사용 용도와는 맞지 않는것 같아 Date타입의 필드를 하나 만들고 저장시간 기준 내림차순 정렬을 사용하기로 결정했습니다.
```kotlin
@Query("SELECT searchWord FROM SearchHistory ORDER BY dateTime DESC LIMIT :limit")
fun getSearchHistory(limit: Int): List<SearchHistory>
/* 동일한 이름의 검색 이력이 있을 경우 중복된 내용도 모두 포함하는 쿼리임. */
```

<b>추가 고려 사항</b>  
* 동일한 검색 기록이 있을 때 가장 최근에 검색된 검색 기록 한개만 가져옵니다.
```kotlin
// (1)-1 동일한 'searchWord'가 있을 경우 MAX(time)인 이력만 조건식 통과
// (1)-2 동일한 'searchWord'가 없으면 자기 자신이 MAX(time)이므로 조건식 통과
// (2) 최종 정렬 기준: time 내림차순
@Query("SELECT searchWord FROM SearchHistory " +
       "WHERE dateTime IN (SELECT MAX(dateTime) FROM SearchHistory GROUP BY searchWord) " + // (1)
       "ORDER BY dateTime DESC " + // (2)
       "LIMIT :limit")
fun getSearchHistory(limit: Int): List<SearchHistory>

/* 로그 */
D/observe: name: 9, time: Sat Apr 23 21:24:51 GMT+09:00 2022
D/observe: name: 8, time: Sat Apr 23 21:24:50 GMT+09:00 2022
D/observe: name: 7, time: Sat Apr 23 21:24:45 GMT+09:00 2022
D/observe: name: 6, time: Sat Apr 23 21:24:34 GMT+09:00 2022
D/observe: name: 13, time: Sat Apr 23 21:24:17 GMT+09:00 2022
D/observe: name: 12, time: Sat Apr 23 21:24:16 GMT+09:00 2022
D/observe: name: 11, time: Sat Apr 23 21:24:14 GMT+09:00 2022
D/observe: name: 10, time: Sat Apr 23 21:24:07 GMT+09:00 2022
D/observe: name: 5, time: Sat Apr 23 21:23:59 GMT+09:00 2022
D/observe: name: 4, time: Sat Apr 23 21:23:58 GMT+09:00 2022
```

### TypeConverter  
```kotlin
/* Converters.class */
class Converters {
  @TypeConverter
  fun fromTimestamp(timestamp: Long): Date = Date(timestamp)

  @TypeConverter
  fun dateToTimestamp(date: Date): Long = date.time
}
```

---
## 기타 고려 사항
1. 영화 제목에 '&' 문장이 포함되어 있을 때 호출 결과가 ```&amp;```로 변경되어 나오는 것을 원래 문자 '&'로 변경하여 출력했습니다.
2. 영화 제목 및 검색 이력이 TextView의 길이 이상으로 내용이 길어지는 경우 한 줄로 강제하고 끝 부분을 '...'으로 바꾸어 축약했습니다.
3. 검색창이 비어있는 경우 API를 호출하지 않으며 Toast를 통해 사용자에게 알립니다.
4. 검색 결과가 없을 경우 Toast를 통해 사용자에게 알립니다.
5. 최근 검색 이력이 없을 때 TextView('검색 내역이 없습니다.')를 표시합니다.
6. 키보드가 열려있을 때 '검색'버튼을 누르면 자동으로 키보드가 내려갑니다.
---
## API Version Test
* <b>api 22 - 정상 작동</b>
* <b>api 23 - 정상 작동</b>
* <b>api 24 - 정상 작동</b>
* <b>api 25 - 정상 작동</b>
* <b>api 26 - 정상 작동</b>
* <b>api 27 - 정상 작동</b>
* <b>api 28 - 정상 작동</b>
* <b>api 29 - 정상 작동</b>
* <b>api 30 - 정상 작동</b>
* <b>api 31 - 정상 작동</b>
---
