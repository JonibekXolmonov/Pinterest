package com.example.pinterest.ui.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.pinterest.R
import com.example.pinterest.model.homephoto.HomePhotoItem
import com.example.pinterest.networking.ApiClient
import com.example.pinterest.networking.services.ApiService
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.imageview.ShapeableImageView
import com.jsibbold.zoomage.ZoomageView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class PhotoDetailFragment : Fragment(R.layout.fragment_photo_detail), View.OnClickListener {

    private lateinit var ivDetailPhoto: ZoomageView
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

    private lateinit var rvLikeThis: RecyclerView

    private lateinit var bottomSheetLayout: CoordinatorLayout
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

        rvLikeThis = view.findViewById(R.id.rvLikeThis)

        ivBack.setOnClickListener(this)
        ivMore.setOnClickListener(this)
        ivShare.setOnClickListener(this)
        tvSave.setOnClickListener(this)


        bottomSheetLayout = view.findViewById(R.id.bottomSheetMore)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        controlBottomSheetAction(bottomSheetLayout)
    }

    private fun controlBottomSheetAction(bottomSheetLayout: CoordinatorLayout) {
        val ivCloseBottomSheet: ImageView = bottomSheetLayout.findViewById(R.id.icCloseBottomSheet)
        val tvCopyLink: TextView = bottomSheetLayout.findViewById(R.id.tvCopyLink)
        val tvDownloadImage: TextView = bottomSheetLayout.findViewById(R.id.tvDownloadImage)

        val imageUrl = arguments?.get("photoUrl").toString()
        val imageID = arguments?.get("photoID").toString()

        ivCloseBottomSheet.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        tvCopyLink.setOnClickListener {
            saveLinkToClipBoard(imageUrl)
            Toast.makeText(requireContext(), "Link copied", Toast.LENGTH_SHORT).show()
        }

        tvDownloadImage.setOnClickListener {
            downLoadImage(imageUrl, imageName = imageID)
        }
    }

    private fun downLoadImage(imageUrl: String, imageName: String) {

    }

    private fun saveLinkToClipBoard(link: String) {
        val clipboard =
            ContextCompat.getSystemService(requireContext(), ClipboardManager::class.java)
        clipboard?.setPrimaryClip(ClipData.newPlainText("", link))
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

                    val source = "Love this Pin? Let " + "<b>${tvUsername.text}<\b>"
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