package com.example.moviesearch

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviesearch.adapter.MovieRecyclerViewAdapter
import com.example.moviesearch.databinding.ActivityMainBinding
import com.example.moviesearch.restapi.NaverOpenApiManager

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val naverOpenApiManager by lazy { NaverOpenApiManager() }
    private val movieRecyclerViewAdapter by lazy { MovieRecyclerViewAdapter(this) }
    private var searchedMovieTitle: String? = null
    private var nextPage: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerViewMovie.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewMovie.adapter = movieRecyclerViewAdapter

        binding.buttonSearch.setOnClickListener { // '검색' 버튼을 클릭했을 때
            movieRecyclerViewAdapter.clearItem()

            if(binding.editTextSearch.text.isEmpty()) {
                Toast.makeText(this, "최소 한 자 이상의 검색어를 입력해 주세요.", Toast.LENGTH_SHORT).show()

            } else {
                val movieTitle = binding.editTextSearch.text.toString()
                naverOpenApiManager.getMovieInfo(partTitle = movieTitle,
                    success = { list, nextPage ->
                        if(list.isEmpty()) {
                            Toast.makeText(this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()

                        } else {
                            // 검색 결과를 추가하기 위해 검색명 쿼리와 next start 쿼리 저장
                            searchedMovieTitle = movieTitle
                            this.nextPage = nextPage

                            movieRecyclerViewAdapter.addItem(list) // Add item
                        }

                    }, failure = {
                        it.printStackTrace()
                    }
                )
            }
        }

        // 리사이클러뷰 스크롤을 마지막까지 내렸을 때 검색 결과 추가
        binding.recyclerViewMovie.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // Cast LinearLayoutManager
                val linearLayoutManager: LinearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                // 마지막 아이템의 position 반환
                val lastItemCount = linearLayoutManager.findLastCompletelyVisibleItemPosition()
                // 전체 아이템 개수
                val totalItemCount = linearLayoutManager.itemCount

                // 최소 한 개 이상의 아이템이 있고, 마지막 아이템을 보고 있는 경우
                if(totalItemCount != 0 && lastItemCount == totalItemCount-1) {
                    if(nextPage > 0) { // 다음 검색이 가능한 경우 Api Call
                        naverOpenApiManager.getMovieInfo(startIndex = nextPage, partTitle = searchedMovieTitle,
                        success = { list, nextPage ->
                            this@MainActivity.nextPage = nextPage
                            movieRecyclerViewAdapter.addItem(list)

                        }, failure = {
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
                // 영화 검색 결과 인텐트 표시
                val movieInfoLink = movieRecyclerViewAdapter.getItem(position).movieLink
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(movieInfoLink))
                startActivity(intent)
            }
        })
    }
}