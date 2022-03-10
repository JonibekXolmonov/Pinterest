package com.example.pinterest.ui.fragments

import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.pinterest.R
import com.example.pinterest.model.homephoto.HomePhotoItem
import com.example.pinterest.networking.ApiClient
import com.example.pinterest.networking.services.ApiService
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PhotoDetailFragment : Fragment(R.layout.fragment_photo_detail), View.OnClickListener {

    private lateinit var ivDetailPhoto: ImageView
    private lateinit var ivProfile: ShapeableImageView
    private lateinit var tvUsername: TextView
    private lateinit var tvNumFollowers: TextView
    private lateinit var tvSave: TextView
    private lateinit var tvComment: TextView
    private lateinit var apiService: ApiService
    private lateinit var ivBack: ImageView
    private lateinit var ivMore: ImageView
    private lateinit var ivShare: ImageView
    private lateinit var navController: NavController

    private lateinit var bottomSheetMore: CoordinatorLayout
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<CoordinatorLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        apiService = ApiClient(requireContext()).createServiceWithAuth(ApiService::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
    }

    private fun initViews(view: View) {
        navController = Navigation.findNavController(view)
        ivDetailPhoto = view.findViewById(R.id.ivDetailPhoto)
        loadImage()

        ivProfile = view.findViewById(R.id.ivProfile)
        tvUsername = view.findViewById(R.id.tvUsername)
        tvNumFollowers = view.findViewById(R.id.tvNumFollowers)

        setLoaderProfile()

        tvComment = view.findViewById(R.id.tvComment)

        ivBack = view.findViewById(R.id.ivBack)
        ivMore = view.findViewById(R.id.ivMore)
        ivShare = view.findViewById(R.id.ivShare)
        tvSave = view.findViewById(R.id.tvSave)

        ivBack.setOnClickListener(this)
        ivMore.setOnClickListener(this)
        ivShare.setOnClickListener(this)
        tvSave.setOnClickListener(this)

        bottomSheetMore = view.findViewById(R.id.bottomSheetMore)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetMore)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun loadImage() {
        arguments.let {
            Picasso.get()
                .load(it?.get("photoUrl").toString())
                .into(ivDetailPhoto)
        }
    }

    private fun setLoaderProfile() {
        apiService.getSelectedPhoto(arguments?.get("photoID").toString())
            .enqueue(object : Callback<HomePhotoItem> {
                override fun onResponse(
                    call: Call<HomePhotoItem>,
                    response: Response<HomePhotoItem>
                ) {
                    val homePhotoItem = response.body() as HomePhotoItem

                    Picasso.get()
                        .load(homePhotoItem.user.profile_image.medium)
                        .into(ivProfile)

                    tvUsername.text = homePhotoItem.user.name

                    val source = "Love this Pin? Let " + "<b>${tvUsername.text}<\b>" + " know"
                    tvComment.text = Html.fromHtml(source)
                }

                override fun onFailure(call: Call<HomePhotoItem>, t: Throwable) {

                }

            })
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ivBack -> {
                navController.navigate(R.id.action_photoDetailFragment_to_homeFragment)
            }
            R.id.ivMore -> {
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                } else {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }
            R.id.ivShare -> {

            }
            R.id.tvSave -> {

            }
        }
    }
}