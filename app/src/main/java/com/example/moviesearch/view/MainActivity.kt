package com.example.moviesearch.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviesearch.adapter.MovieRecyclerViewAdapter
import com.example.moviesearch.databinding.ActivityMainBinding
import com.example.moviesearch.restapi.naversearchapi.NaverSearchManager
import com.example.moviesearch.room.entities.SearchHistory
import com.example.moviesearch.viewmodel.LocalDatabaseViewModel
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val naverOpenApiManager by lazy { NaverSearchManager() }
    private val movieRecyclerViewAdapter by lazy { MovieRecyclerViewAdapter(this) }
    private val localDatabaseViewModel by lazy {
        ViewModelProvider(this).get(LocalDatabaseViewModel::class.java)
    }
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var searchedWord: String
    private var nextIndex: Int = -1
    private val keyboard by lazy { getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup RecyclerView
        binding.recyclerViewMovie.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewMovie.adapter = movieRecyclerViewAdapter

        binding.searchButton.setOnClickListener { // '검색' 버튼을 클릭했을 때
            movieRecyclerViewAdapter.clearItem()
            keyboard.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0) // 키보드 숨기기

            if(binding.searchEditText.text.isEmpty()) {
                Toast.makeText(this, "최소 한 자 이상의 검색어를 입력해 주세요.", Toast.LENGTH_SHORT).show()

            } else { // EditText 에 글자가 입력된 경우
                searchedWord = binding.searchEditText.text.toString()
                insertSearchHistory(searchedWord) // 검색 이력 저장

                // 검색 결과 호출(rest api)
                naverOpenApiManager.getMovieInfo(
                    searchWord = searchedWord,
                    onSuccess = { movieList, nextIndex -> // callback
                        if(movieList.isEmpty()) {
                            Toast.makeText(this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()

                        } else {
                            // 다음 검색 결과를 위해 nextIndex 저장
                            this.nextIndex = nextIndex
                            movieRecyclerViewAdapter.addItem(movieList) // Add item
                        }
                    },
                    onFailure = { errorCode ->  // callback
                        showToastFailureResponse(errorCode)
                    },
                    onError = { throwable -> // callback
                        showToastErrorResponse(throwable)
                    }
                )
            }
        }

        // 리사이클러뷰 스크롤을 마지막까지 내렸을 때 검색 결과 추가
        binding.recyclerViewMovie.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val linearLayoutManager: LinearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                // 리사이클러뷰에 보여지고 있는 마지막 아이템의 position
                val lastVisibleItemPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition()
                val totalItemCount = linearLayoutManager.itemCount // 전체 아이템 개수

                // 최소 한 개 이상의 아이템이 있고, 마지막 아이템을 보고 있는 경우
                if(totalItemCount != 0 && lastVisibleItemPosition == totalItemCount-1) {
                    if(nextIndex > 0) { // 결과가 더 있는 경우에만 API call
                        naverOpenApiManager.getMovieInfo(
                            startIndex = nextIndex,
                            searchWord = searchedWord,
                            onSuccess = { movieList, nextIndex ->
                                this@MainActivity.nextIndex = nextIndex
                                movieRecyclerViewAdapter.addItem(movieList)
                            },
                            onFailure = { errorCode ->
                                showToastFailureResponse(errorCode)
                            },
                            onError = { throwable ->
                                showToastErrorResponse(throwable)
                            })
                    } else {
                        Toast.makeText(this@MainActivity, "마지막 결과입니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

        // RecyclerView Item click event
        movieRecyclerViewAdapter.setOnItemClickListener(object: MovieRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                // 영화 정보 인텐트
                val movieInfoLink = movieRecyclerViewAdapter.getItem(position).infoLink
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(movieInfoLink))
                startActivity(intent)
            }
        })

        // Start 'SearchHistoryActivity' for Result
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == RESULT_OK) { // 최근 검색 이력을 클릭했을 때만 실행
                movieRecyclerViewAdapter.clearItem()

                result.data?.getStringExtra("SEARCH_WORD")?.let {
                    searchedWord = it // 다음 검색 결과를 위해 전역 변수에 검색어를 저장
                    binding.searchEditText.setText(searchedWord)
                    insertSearchHistory(searchedWord) // 검색 이력 저장

                    // 검색 결과 호출(rest api)
                    naverOpenApiManager.getMovieInfo(
                        searchWord = searchedWord,
                        onSuccess = { movieList, nextIndex ->
                            if(movieList.isEmpty()) {
                                Toast.makeText(this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()

                            } else {
                                this.nextIndex = nextIndex
                                movieRecyclerViewAdapter.addItem(movieList) // Add item
                            }
                        },
                        onFailure = { errorCode ->
                            showToastFailureResponse(errorCode)
                        },
                        onError = { throwable ->
                            showToastErrorResponse(throwable)
                        })
                }
            }
        }

        // '최근검색' button click event
        binding.searchHistoryButton.setOnClickListener {
            val intent = Intent(this, SearchHistoryActivity::class.java)
            activityResultLauncher.launch(intent)
        }

        // Room DB - 검색 이력 삽입 에러 발생 observer
        localDatabaseViewModel.isInsertFailedLiveData.observe(this, {
            Toast.makeText(this, "검색 이력 저장을 실패했습니다.", Toast.LENGTH_SHORT).show()
        })
    }

    // 검색 이력 저장
    private fun insertSearchHistory(searchWord: String) {
        val searchHistoryDTO = SearchHistory(
            dateTime = Date(System.currentTimeMillis()),
            searchWord = searchWord
        )

        localDatabaseViewModel.insertHistory(searchHistoryDTO)
    }

    // rest 서버에서 에러 코드 응답 시 토스트
    private fun showToastFailureResponse(errorCode: Int) {
        when(errorCode) {
            400 -> Toast.makeText(this, "잘못된 검색어입니다.", Toast.LENGTH_SHORT).show()
            500 -> Toast.makeText(this, "서버 내부 에러가 발생하였습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // rest 서버 응답 실패 시 토스트
    private fun showToastErrorResponse(throwable: Throwable) {
        when(throwable) {
            is UnknownHostException ->
                Toast.makeText(this, "네트워크 연결을 확인해 주세요.", Toast.LENGTH_SHORT).show()

            is SocketTimeoutException ->
                Toast.makeText(this, "네트워크 연결을 확인해 주세요.", Toast.LENGTH_SHORT).show()
        }
    }
}