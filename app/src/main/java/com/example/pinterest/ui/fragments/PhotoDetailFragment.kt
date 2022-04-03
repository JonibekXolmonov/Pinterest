package com.example.pinterest.ui.fragments

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
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
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


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
    private lateinit var nestedScrollView: NestedScrollView

    private lateinit var rvLikeThis: RecyclerView
    private lateinit var staggeredGridLayoutManager: StaggeredGridLayoutManager
    var relatedPhotoAdapter = SearchPhotoAdapter()

    private var PAGE = 1
    private var PAGE_RELATED = 1
    private var PER_PAGE = 20
    private var PER_PAGE_RELATED = 20

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedElementEnterTransition
        return super.onCreateView(inflater, container, savedInstanceState)
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
        nestedScrollView = view.findViewById(R.id.nestedScrollView)

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

        addLoadingMore()

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

    private fun addLoadingMore() {
        nestedScrollView.setOnScrollChangeListener { v: NestedScrollView, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->
            if (v.getChildAt(v.childCount - 1) != null) {
                if (scrollY >= v.getChildAt(v.childCount - 1).measuredHeight - v.measuredHeight && scrollY > oldScrollY) {
                    getRelated()
                }
            }
        }
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
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            Toast.makeText(requireContext(), "Link copied", Toast.LENGTH_SHORT).show()
        }

        tvDownloadImage.setOnClickListener {
            apiService.getSelectedPhoto(imageID).enqueue(object : Callback<HomePhotoItem> {
                override fun onResponse(
                    call: Call<HomePhotoItem>,
                    response: Response<HomePhotoItem>
                ) {
                    if (isPermissionGranted()) {
                        downLoadImage(response.body()!!.links.download, imageName = imageID)
                    } else {
                        getPermission()
                        downLoadImage(response.body()!!.links.download, imageName = imageID)
                    }
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
                }

                private fun getPermission() {
                    val STORAGE_PERMISSIONS =
                        arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    // If permission not granted then ask for permission real time.
                    ActivityCompat.requestPermissions(requireActivity(), STORAGE_PERMISSIONS, 1)
                }

                private fun isPermissionGranted(): Boolean = ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED

                override fun onFailure(call: Call<HomePhotoItem>, t: Throwable) {

                }
            })
        }
    }

    private fun downLoadImage(imageURL: String?, imageName: String) {
        Glide.with(this)
            .load(imageURL)
            .into(object : CustomTarget<Drawable?>() {

                override fun onResourceReady(
                    resource: Drawable,
                    @Nullable transition: Transition<in Drawable?>?
                ) {
                    val bitmap = (resource as BitmapDrawable).bitmap

                    saveImage(bitmap, imageName)
                }

                override fun onLoadCleared(@Nullable placeholder: Drawable?) {}
            })
    }

    private fun saveImage(image: Bitmap, imageFileName: String) {


        //Generating a file name
        val filename = "${imageFileName}.jpg"

        //Output stream
        var fos: OutputStream? = null

//        For devices running android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //getting the contentResolver
            context?.contentResolver?.also { resolver ->

                //Content resolver will process the content values
                val contentValues = ContentValues().apply {

                    //putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                //Inserting the contentValues to contentResolver and getting the Uri
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                //Opening an outputstream with the Uri that we got
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            //These for devices running on android < Q
            //So I don't think an explanation is needed here
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {
            //Finally writing the bitmap to the output stream that we opened
            image.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
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

    private fun onBackPressed(){
        requireActivity().onBackPressed()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ivBack -> {
                onBackPressed()
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