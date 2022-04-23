# Subject_MovieSearch
---
## 기술 스택
### Retrofit - 네이버 영화 검색 API 호출
<b>추가 고려 사항</b>  
* 네이버 검색 API를 한번 호출 했을 때 출력 결과 건수는 10건(Default)이므로 다음 페이지 출력을 위해 '검색의 시작 위치+검색 결과 출력 건수(start+display)=다음 검색의 시작위치'를 결과 리스트와 함께 콜백합니다. 다음 검색의 시작 위치를 알고 있으므로 리사이클러뷰가 끝까지 스크롤되었을 때 다음 검색의 시작위치부터 10건의 검색 결과를 호출할 수 있습니다.

* 사용자의 디바이스가 네트워크를 사용할 수 없을 경우 Failure를 콜백합니다.

* 검색 결과의 영화 제목에서 검색어와 일치하는 부분은 <b></b> 태그로 감싸져 있기 때문에 replace 메소드를 통해 태그를 삭제한 값으로 이름을 다시 저장합니다.
&nbsp;
### Glide - 영화 썸네일 이미지 로딩 및 리사이클러뷰 탑재
<b>추가 고려 사항</b>  
* 검색 API의 결과에 영화 Link Url은 존재하지 않을 수 있으므로 Glide.error()를 통해 대체 이미지를 표시합니다.
&nbsp;
### Room - 최근 검색 이력 저장을 위한 로컬 데이터베이스
### 테이블 설계  
<b>SearchHistory</b>
|Field|Type|Null|Pri|Ex|
|---|---|---|---|---|
|id|Int|NotNull|Pri|순번(auto increment)|
|time|Date|NotNull||시간 기준 내림차순 정렬을 위한 필드|
|searchName|String|NotNull||검색어|

### 쿼리
* time 기준 내림차순 정렬/ limit으로 검색 결과 개수 제한
```kotlin
@Query("SELECT searchName FROM SearchHistory ORDER BY time DESC LIMIT :limit")
fun getSearchHistory(limit: Int): List<SearchHistory>
/* 동일한 이름의 검색 이력이 있을 경우 중복된 내용도 모두 포함하는 쿼리임.*/
```

<b>추가 고려 사항</b>  
* 동일한 검색 기록이 있을 때 가장 최근에 검색된 검색 기록 한개만 가져옵니다.
```kotlin
/* 검색명 기준으로 그룹화 후 time 기준 내림차순 정렬 / limit으로 검색 결과 개수 제한 */
@Query("SELECT * FROM SearchHistory GROUP BY searchName ORDER BY time DESC LIMIT :limit")
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
* Auto Increment를 통한 내림차순 정렬도 가능하나, 사용 용도와는 맞지 않는것 같아 Date타입의 필드를 하나 만들고 저장시간 기준 내림차순 정렬을 사용하기로 결정했습니다.
```
/* Converters.class */
class Converters {
  @TypeConverter
  fun fromTimestamp(timestamp: Long): Date = Date(timestamp)

  @TypeConverter
  fun dateToTimestamp(date: Date): Long = date.time
}

/* 로그 */
D/debug: name: good, date: Sat Apr 23 20:21:58 GMT+09:00 2022
D/debug: name: hi2, date: Sat Apr 23 20:21:50 GMT+09:00 2022
D/debug: name: hi, date: Sat Apr 23 20:21:36 GMT+09:00 2022
```

---
## 기타 고려 사항
1. 영화 제목이 TextView의 길이 이상으로 내용이 길어지는 경우 한 줄로 강제하고 끝 부분을 '...'으로 바꾸어 축약했습니다.
---
## API Version Test
* <b>api 23 - 정상 작동</b>
* <b>api 24 - 정상 작동</b>
* <b>api 25 - 정상 작동</b>
* <b>api 26 - 정상 작동</b>
* <b>api 27 - 정상 작동</b>
* <b>api 28 - 정상 작동</b>
* <b>api 29 - 정상 작동</b>
* <b>api 30 - 정상 작동</b>
* <b>api 31 - 정상 작동</b>
