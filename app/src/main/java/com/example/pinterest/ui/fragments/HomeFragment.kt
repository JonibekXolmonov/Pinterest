package com.example.pinterest.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment

import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.pinterest.R
import com.example.pinterest.adapter.HomePhotoAdapter
import com.example.pinterest.helper.EndlessRecyclerViewScrollListener
import com.example.pinterest.model.homephoto.HomePhotoItem
import com.example.pinterest.networking.ApiClient
import com.example.pinterest.networking.services.ApiService
import com.example.pinterest.ui.fragments.helper.SpaceItemDecoration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var homePhotoAdapter: HomePhotoAdapter
    private lateinit var rvHomePhotos: RecyclerView
    private lateinit var apiService: ApiService
    private lateinit var staggeredGridLayoutManager: StaggeredGridLayoutManager
    private lateinit var navController: NavController

    private var PAGE = 1
    private var PER_PAGE = 20

    private lateinit var list: ArrayList<HomePhotoItem>

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

        list = ArrayList()
        navController = Navigation.findNavController(view)

        getPhotos()
        refreshAdapter(list)

        val scrollListener = object : EndlessRecyclerViewScrollListener(
            staggeredGridLayoutManager
        ) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                getPhotos()
            }
        }
        rvHomePhotos.addOnScrollListener(scrollListener)

        controlItemClick()
    }

    private fun controlItemClick() {
        homePhotoAdapter.photoClick = {
            navController.navigate(
                R.id.action_homeFragment_to_photoDetailFragment,
                bundleOf("photoID" to it.id, "photoUrl" to it.urls.regular)
            )
        }
    }

    private fun refreshAdapter(list: ArrayList<HomePhotoItem>) {
        homePhotoAdapter = HomePhotoAdapter(list)
        rvHomePhotos.adapter = homePhotoAdapter
    }


    private fun getPhotos() {
        apiService.getPhotos(PAGE++, PER_PAGE).enqueue(object : Callback<List<HomePhotoItem>> {
            override fun onResponse(
                call: Call<List<HomePhotoItem>>,
                response: Response<List<HomePhotoItem>>
            ) {
                list.addAll(response.body()!!)
                homePhotoAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<List<HomePhotoItem>>, t: Throwable) {

            }
        })
    }

}