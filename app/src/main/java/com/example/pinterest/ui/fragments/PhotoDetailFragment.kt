package com.example.pinterest.ui.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.pinterest.R
import com.example.pinterest.adapter.SearchPhotoAdapter
import com.example.pinterest.database.Saved
import com.example.pinterest.database.SavedDatabase
import com.example.pinterest.helper.EndlessRecyclerViewScrollListener
import com.example.pinterest.helper.SpaceItemDecoration
import com.example.pinterest.model.homephoto.HomePhotoItem
import com.example.pinterest.model.relatedcollection.SinglePhoto
import com.example.pinterest.model.search.ResponseSearch
import com.example.pinterest.networking.ApiClient
import com.example.pinterest.networking.services.ApiService
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.imageview.ShapeableImageView
import com.jsibbold.zoomage.ZoomageView
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PhotoDetailFragment : Fragment(R.layout.fragment_photo_detail), View.OnClickListener {

    private lateinit var apiService: ApiService

    private lateinit var ivDetailPhoto: ZoomageView
    private lateinit var ivProfile: ShapeableImageView
    private lateinit var tvUsername: TextView
    private lateinit var tvNumFollowers: TextView
    private lateinit var tvSave: TextView
    private lateinit var tvComment: TextView
    private lateinit var ivBack: ImageView
    private lateinit var ivMore: ImageView
    private lateinit var ivShare: ImageView
    private lateinit var navController: NavController

    private lateinit var rvLikeThis: RecyclerView
    private lateinit var staggeredGridLayoutManager: StaggeredGridLayoutManager
    var relatedPhotoAdapter = SearchPhotoAdapter()

    private var PAGE = 1
    private var PAGE_RELATED = 1
    private var PER_PAGE = 20
    private var PER_PAGE_RELATED = 40

    private lateinit var bottomSheetLayout: CoordinatorLayout
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<CoordinatorLayout>

    private lateinit var savedDatabase: SavedDatabase
    lateinit var imageUrl: String
    lateinit var imageID: String
    lateinit var imageDescription: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        apiService = ApiClient(requireContext()).createServiceWithAuth(ApiService::class.java)
        imageUrl = arguments?.get("photoUrl").toString()
        imageID = arguments?.get("photoID").toString()
        imageDescription = arguments?.get("description").toString()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
    }

    private fun initViews(view: View) {
        navController = Navigation.findNavController(view)
        ivDetailPhoto = view.findViewById(R.id.ivDetailPhoto)
        loadImage(imageUrl)

        ivProfile = view.findViewById(R.id.ivProfile)
        tvUsername = view.findViewById(R.id.tvUsername)
        tvNumFollowers = view.findViewById(R.id.tvNumFollowers)

        setLoaderProfile()

        tvComment = view.findViewById(R.id.tvComment)

        ivBack = view.findViewById(R.id.ivBack)
        ivMore = view.findViewById(R.id.ivMore)
        ivShare = view.findViewById(R.id.ivShare)
        tvSave = view.findViewById(R.id.tvSave)

        if (existInDatabase(imageID)) {
            tvSave.text = "Saved!"
            tvSave.setTextColor(Color.parseColor("#000000"))
            tvSave.setBackgroundColor(Color.parseColor("#ffffff"))
        }

        rvLikeThis = view.findViewById(R.id.rvLikeThis)
        staggeredGridLayoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rvLikeThis.layoutManager = staggeredGridLayoutManager
        rvLikeThis.addItemDecoration(SpaceItemDecoration(20))
        relatedPhotoAdapter = SearchPhotoAdapter()
        rvLikeThis.adapter = relatedPhotoAdapter


        getRelated()
        controlClick()


        ivBack.setOnClickListener(this)
        ivMore.setOnClickListener(this)
        ivShare.setOnClickListener(this)
        tvSave.setOnClickListener(this)


        bottomSheetLayout = view.findViewById(R.id.bottomSheetMore)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        controlBottomSheetAction(bottomSheetLayout)

        savedDatabase = SavedDatabase.getInstance(requireContext())
    }

    private fun existInDatabase(id: String): Boolean {
        val savedDatabase = SavedDatabase.getInstance(requireContext())
        return savedDatabase.savedDao().count(id) > 0
    }

    private fun controlClick() {
        relatedPhotoAdapter.photoClick = {
            navController.navigate(
                R.id.action_photoDetailFragment_self,
                bundleOf(
                    "photoID" to it.id,
                    "photoUrl" to it.urls.regular,
                    "description" to it.description
                )
            )
        }
    }

    private fun getRelated() {
        apiService.getImageToRelated(imageID, PAGE_RELATED, PER_PAGE_RELATED)
            .enqueue(object : Callback<SinglePhoto> {
                override fun onResponse(call: Call<SinglePhoto>, response: Response<SinglePhoto>) {
                    val tags = response.body()!!.related_collections.results[0].tags
                    var query = ""
                    for (i in 0 until tags.size - 1)
                        query += tags[i].title + " "

                    getSearchResults(query.trim())
                }

                override fun onFailure(call: Call<SinglePhoto>, t: Throwable) {
                    Log.d("TAG", "onFailure: ${t.localizedMessage}")
                }
            })
    }

    private fun getSearchResults(query: String) {
        apiService.searchPhotos(query, PAGE++, PER_PAGE)
            .enqueue(object : Callback<ResponseSearch> {
                override fun onResponse(
                    call: Call<ResponseSearch>,
                    response: Response<ResponseSearch>
                ) {
                    relatedPhotoAdapter.submitData(response.body()!!.results)
                }

                override fun onFailure(call: Call<ResponseSearch>, t: Throwable) {
                }
            })
    }

    private fun controlBottomSheetAction(bottomSheetLayout: CoordinatorLayout) {
        val ivCloseBottomSheet: ImageView = bottomSheetLayout.findViewById(R.id.icCloseBottomSheet)
        val tvCopyLink: TextView = bottomSheetLayout.findViewById(R.id.tvCopyLink)
        val tvDownloadImage: TextView = bottomSheetLayout.findViewById(R.id.tvDownloadImage)

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

    private fun loadImage(imageUrl: String) {
        Picasso.get()
            .load(imageUrl)
            .into(ivDetailPhoto)
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
                shareImageUrl(imageUrl)
            }
            R.id.tvSave -> {
                savedToDatabase(imageID, imageUrl, imageDescription)
                tvSave.setTextColor(Color.parseColor("#000000"))
                tvSave.setBackgroundColor(Color.parseColor("#ffffff"))
            }
        }
    }

    private fun shareImageUrl(imageUrl: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, imageUrl)
        startActivity(Intent.createChooser(intent, "Choose receiver"))
    }

    private fun savedToDatabase(imageID: String, imageUrl: String, imageDescription: String) {
        if (imageDescription != "null") {
            savedDatabase.savedDao().insertProduct(Saved(imageID, imageUrl, imageDescription))
        } else {
            savedDatabase.savedDao().insertProduct(Saved(imageID, imageUrl, ""))
        }
        Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show()
    }
}