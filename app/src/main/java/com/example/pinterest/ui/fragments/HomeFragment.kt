package com.example.pinterest.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment

import android.view.View
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigator
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.pinterest.R
import com.example.pinterest.adapter.HomePhotoAdapter
import com.example.pinterest.adapter.TopicAdapter
import com.example.pinterest.helper.EndlessRecyclerViewScrollListener
import com.example.pinterest.model.homephoto.HomePhotoItem
import com.example.pinterest.networking.ApiClient
import com.example.pinterest.networking.services.ApiService
import com.example.pinterest.helper.SpaceItemDecoration
import com.example.pinterest.model.topic.Topic
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment(R.layout.fragment_home) {

    private var homePhotoAdapter: HomePhotoAdapter = HomePhotoAdapter()
    private lateinit var rvHomePhotos: RecyclerView
    private lateinit var staggeredGridLayoutManager: StaggeredGridLayoutManager

    private lateinit var rvHomeTopics: RecyclerView
    private lateinit var topicID: String
    private var topicAdapter: TopicAdapter = TopicAdapter()

    private lateinit var apiService: ApiService

    private lateinit var navController: NavController

    private var PAGE = 1
    private var PER_PAGE = 30

    private var PAGE_TOPIC = 1
    private var PER_PAGE_TOPIC = 30

    private var TYPE: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        apiService = ApiClient(requireContext()).createServiceWithAuth(ApiService::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
    }

    private fun initViews(view: View) {
        rvHomePhotos = view.findViewById(R.id.rvHomePhotos)
        staggeredGridLayoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rvHomePhotos.layoutManager = staggeredGridLayoutManager
        rvHomePhotos.addItemDecoration(SpaceItemDecoration(20))

        rvHomeTopics = view.findViewById(R.id.rvHomeTopics)

        navController = Navigation.findNavController(view)

        getTopics()
        refreshTopicAdapter()

        getPhotos()
        refreshAdapter()

        addLoadingMore()

        controlTopicClick()

        controlItemClick()
    }

    private fun controlTopicClick() {
        topicAdapter.topicClick = { topic, position ->
            if (position == 0) {
                getPhotos()
                TYPE = 0
            } else {
                homePhotoAdapter.photos.clear()
                PAGE_TOPIC = 1
                TYPE = 1
                topicID = topic.id
                getTopicPhotos()
            }
        }
    }

    private fun getTopicPhotos() {
        apiService.getTopicPhotos(topicID, PAGE_TOPIC++, PER_PAGE_TOPIC)
            .enqueue(object : Callback<List<HomePhotoItem>> {
                override fun onResponse(
                    call: Call<List<HomePhotoItem>>,
                    response: Response<List<HomePhotoItem>>
                ) {
                    homePhotoAdapter.submitData(response.body()!!)
                }

                override fun onFailure(call: Call<List<HomePhotoItem>>, t: Throwable) {

                }

            })
    }

    private fun refreshTopicAdapter() {
        rvHomeTopics.adapter = topicAdapter
    }

    private fun getTopics() {
        apiService.getTopics().enqueue(object : Callback<List<Topic>> {
            override fun onResponse(call: Call<List<Topic>>, response: Response<List<Topic>>) {
                Log.d("TAG", "onResponse: ${response.body()}")
                if (response.body()!!.isNotEmpty())
                    topicAdapter.submitData(response.body()!!)
            }

            override fun onFailure(call: Call<List<Topic>>, t: Throwable) {
                Log.d("TAG", "onFailure: ${t.toString()}")
            }

        })
    }

    private fun addLoadingMore() {
        val scrollListener = object : EndlessRecyclerViewScrollListener(
            staggeredGridLayoutManager
        ) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                if (TYPE == 0)
                    getPhotos()
                else
                    getTopicPhotos()
            }
        }
        rvHomePhotos.addOnScrollListener(scrollListener)
    }

    private fun controlItemClick() {
        homePhotoAdapter.photoClick = { homePhotoItem: HomePhotoItem ->
            navController.navigate(
                R.id.action_homeFragment_to_photoDetailFragment,
                bundleOf(
                    "photoID" to homePhotoItem.id,
                    "photoUrl" to homePhotoItem.urls.regular,
                    "description" to homePhotoItem.description
                )
            )
        }
    }

    private fun refreshAdapter() {
        rvHomePhotos.adapter = homePhotoAdapter
    }


    private fun getPhotos() {
        apiService.getPhotos(PAGE++, PER_PAGE).enqueue(object : Callback<List<HomePhotoItem>> {
            override fun onResponse(
                call: Call<List<HomePhotoItem>>,
                response: Response<List<HomePhotoItem>>
            ) {
                homePhotoAdapter.submitData(response.body()!!)
            }

            override fun onFailure(call: Call<List<HomePhotoItem>>, t: Throwable) {

            }
        })
    }

}