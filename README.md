# Subject_MovieSearch
## 기술 스택
---
### Retrofit - 네이버 영화 검색 API 호출
<b>추가 고려 사항</b>
```
네이버 검색 API를 한번 호출 했을 때 출력 결과 건수는 10건(Default)이므로 다음 페이지 출력을 위해 
'검색의 시작 위치+검색 결과 출력 건수(start+display)=다음 검색의 시작위치'를 결과 리스트와 함께 콜백합니다.
```
```
사용자의 디바이스가 네트워크를 사용할 수 없을 경우 Failure를 콜백합니다.
```
```
검색 결과의 영화 제목에서 검색어와 일치하는 부분은 <b></b> 태그 감싸져 있기 때문에
replace 메소드를 통해 태그를 삭제한 값으로 이름을 다시 저장합니다.
```
---
### Glide - 영화 썸네일 이미지 로딩 및 리사이클러뷰 탑재
<b>추가 고려 사항</b>
```
검색 API의 결과에 영화 Link Url은 존재하지 않을 수 있으므로 Glide error handling을 통해 대체 이미지를 표시합니다.
```

## API Version Test
* <b>api 23 - 정상 작동</b>
* <b>api 24 - 정상 작동</b>
* <b>api 25 - 정상 작동</b>
* <b>api 26 - 정상 작동</b>
* <b>api 27 - 정상 작동</b>
* <b>api 28 - 정상 작동</b>
* <b>api 29 - 정상 작동</b>
* <b>api 30 - 정상 작동</b>
* <b>api 31 - GlideException: Failed to load resource</b>
