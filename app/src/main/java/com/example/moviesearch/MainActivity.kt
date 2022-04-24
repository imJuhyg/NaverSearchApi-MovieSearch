package com.example.moviesearch

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviesearch.adapter.MovieRecyclerViewAdapter
import com.example.moviesearch.databinding.ActivityMainBinding
import com.example.moviesearch.restapi.naveropenapi.NaverOpenApiManager
import com.example.moviesearch.room.entities.SearchHistory
import com.example.moviesearch.viewmodel.LocalDatabaseViewModel
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val naverOpenApiManager by lazy { NaverOpenApiManager() }
    private val movieRecyclerViewAdapter by lazy { MovieRecyclerViewAdapter(this) }
    private val localDatabaseViewModel by lazy {
        ViewModelProvider(this).get(LocalDatabaseViewModel::class.java)
    }
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var searchedWord: String? = null
    private var nextIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup RecyclerView
        binding.recyclerViewMovie.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewMovie.adapter = movieRecyclerViewAdapter

        binding.buttonSearch.setOnClickListener { // '검색' 버튼을 클릭했을 때
            movieRecyclerViewAdapter.clearItem()

            if(binding.editTextSearch.text.isEmpty()) {
                Toast.makeText(this, "최소 한 자 이상의 검색어를 입력해 주세요.", Toast.LENGTH_SHORT).show()

            } else { // EditText 에 글자가 입력된 경우
                val searchWord = binding.editTextSearch.text.toString()

                val searchHistoryDTO = SearchHistory(
                    dateTime = Date(System.currentTimeMillis()),
                    searchWord = searchWord
                )
                localDatabaseViewModel.insertHistory(searchHistoryDTO) // Room - 검색 이력 저장

                // REST API - 검색 결과 호출
                naverOpenApiManager.getMovieInfo(searchWord = searchWord,
                    success = { movieList, nextIndex ->
                        if(movieList.isEmpty()) {
                            Toast.makeText(this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()

                        } else {
                            // 검색 결과를 추가하기 위해 검색명 쿼리와 next start 쿼리 저장
                            searchedWord = searchWord
                            this.nextIndex = nextIndex
                            // TODO 이게 좀 마음에 안듦

                            movieRecyclerViewAdapter.addItem(movieList) // Add item
                        }

                    }, failure = {
                        // TODO Error handling
                        it.printStackTrace()
                    }
                )
            }
        }

        // 리사이클러뷰 스크롤을 마지막까지 내렸을 때 검색 결과 추가
        binding.recyclerViewMovie.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val linearLayoutManager: LinearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastItemCount = linearLayoutManager.findLastCompletelyVisibleItemPosition() // 마지막 아이템의 position 반환
                val totalItemCount = linearLayoutManager.itemCount // 전체 아이템 개수

                // 최소 한 개 이상의 아이템이 있고, 마지막 아이템을 보고 있는 경우
                if(totalItemCount != 0 && lastItemCount == totalItemCount-1) {
                    if(nextIndex > 0) { // 다음 검색이 가능한 경우 Api Call(더 이상 호출할 결과가 없을 경우 nextPage == -1)
                        naverOpenApiManager.getMovieInfo(startIndex = nextIndex, searchWord = searchedWord,
                        success = { movieList, nextIndex ->
                            this@MainActivity.nextIndex = nextIndex
                            movieRecyclerViewAdapter.addItem(movieList)

                        }, failure = {
                            // TODO Error Handling
                            it.printStackTrace()
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

                result.data?.getStringExtra("SEARCH_WORD")?.let { searchWord ->
                    binding.editTextSearch.setText(searchWord)

                    val searchHistoryDTO = SearchHistory(
                        dateTime = Date(System.currentTimeMillis()),
                        searchWord = searchWord
                    )
                    localDatabaseViewModel.insertHistory(searchHistoryDTO) // 검색 이력 저장

                    // 검색 결과 호출
                    naverOpenApiManager.getMovieInfo(searchWord = searchWord,
                        success = { movieList, nextIndex ->
                            if(movieList.isEmpty()) {
                                Toast.makeText(this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()

                            } else {
                                // 검색 결과를 추가하기 위해 검색명 쿼리와 next start 쿼리 저장
                                searchedWord = searchWord
                                this.nextIndex = nextIndex

                                movieRecyclerViewAdapter.addItem(movieList) // Add item
                            }

                        }, failure = {
                            // TODO Error handling
                            it.printStackTrace()
                        }
                    )
                }
            }
        }
        binding.buttonSearchHistory.setOnClickListener { // '최근검색' click event
            val intent = Intent(this, SearchHistoryActivity::class.java)
            activityResultLauncher.launch(intent)
        }
    }
}