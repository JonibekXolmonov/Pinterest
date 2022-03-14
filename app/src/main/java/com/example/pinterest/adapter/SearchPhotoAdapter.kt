package com.example.pinterest.adapter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pinterest.R
import com.example.pinterest.model.homephoto.HomePhotoItem
import com.example.pinterest.model.search.ResponseSearch
import com.example.pinterest.model.search.Result
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class SearchPhotoAdapter() :
    RecyclerView.Adapter<SearchPhotoAdapter.HomePhotoVH>() {

    private var photos: ArrayList<Result> = ArrayList()
    lateinit var photoClick: ((Result) -> Unit)

    inner class HomePhotoVH(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(photo: Result) {
            val ivHomePhoto: ImageView = view.findViewById(R.id.ivHomePhoto)
            val tvHomePhotoTitle: TextView = view.findViewById(R.id.tvHomePhotoTitle)

            Picasso.get()
                .load(photo.urls.thumb)
                .placeholder(ColorDrawable(Color.parseColor(photo.color)))
                .into(ivHomePhoto, object : Callback {
                    override fun onSuccess() {
                        if (photo.description != null) {
                            tvHomePhotoTitle.text = photo.description
                        }
                    }

                    override fun onError(e: Exception?) {

                    }
                })

            ivHomePhoto.setOnClickListener {
                photoClick.invoke(photo)
            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePhotoVH {
        return HomePhotoVH(
            LayoutInflater.from(parent.context).inflate(R.layout.item_home_photo, parent, false)
        )
    }

    override fun onBindViewHolder(holder: HomePhotoVH, position: Int) {
        holder.bind(getItem(position))

    }

    fun submitData(list: List<Result>) {
        photos.addAll(list)
        notifyDataSetChanged()
    }

    fun clearList() {
        photos.clear()
        notifyDataSetChanged()
    }

    private fun getItem(position: Int): Result = photos[position]

    override fun getItemCount(): Int = photos.size
}