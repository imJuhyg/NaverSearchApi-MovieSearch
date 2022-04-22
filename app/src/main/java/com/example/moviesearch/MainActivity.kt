package com.example.moviesearch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moviesearch.adapter.MovieRecyclerView
import com.example.moviesearch.databinding.ActivityMainBinding
import com.example.moviesearch.searchapi.NaverOpenApiManager
import com.example.moviesearch.searchapi.RetrofitManager

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val naverOpenApiManager by lazy { NaverOpenApiManager() }
    private val movieRecyclerViewAdapter by lazy { MovieRecyclerView(this) }
    private var searchedMovieTitle: String? = null
    private var nextPage: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerViewMovie.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewMovie.adapter = movieRecyclerViewAdapter

        binding.buttonSearch.setOnClickListener {
            movieRecyclerViewAdapter.clearItem()
            // TODO editText == ""일 때 검색어를 입력해주세요 Toast
            val movieTitle = binding.editTextSearch.text.toString()
            naverOpenApiManager.getMovieInfo(partTitle = movieTitle,
                success = { list, nextPage ->
                    // TODO
                    // 1. partTitle 전역 변수에 저장
                    searchedMovieTitle = movieTitle
                    // 2. nextPage 전역 변수에 저장
                    this.nextPage = nextPage
                    // 3. list로 리사이클러뷰 탑제
                    movieRecyclerViewAdapter.addItem(list)
                    // 4. 리사이클러뷰 끝까지 스크롤하면 nextPage 표시

                }, failure = {

                }
            )
        }
    }
}