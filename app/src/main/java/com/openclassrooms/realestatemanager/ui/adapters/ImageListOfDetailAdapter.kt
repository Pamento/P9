package com.openclassrooms.realestatemanager.ui.adapters

import android.net.Uri
import com.openclassrooms.realestatemanager.data.local.entities.ImageOfProperty
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.ui.adapters.ImageListOfDetailAdapter.DetailImageViewHolder
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import com.bumptech.glide.request.RequestOptions
import android.widget.TextView
import com.openclassrooms.realestatemanager.databinding.ItemDetailImageBinding

class ImageListOfDetailAdapter(private val mImageOfPropertyList: List<ImageOfProperty>?) :
    RecyclerView.Adapter<DetailImageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailImageViewHolder {
        val binding = ItemDetailImageBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailImageViewHolder, position: Int) {
        if (mImageOfPropertyList != null) {
            val propertyImg = mImageOfPropertyList[position]
            val uri = Uri.parse(propertyImg.path)
            if (uri != null) {
                Glide.with(holder.mDetailImgItem.context)
                    .load(uri)
                    .error(R.drawable.image_not_found_square)
                    .placeholder(R.drawable.image_placeholder)
                    .transform(RoundedCornersTransformation(20, 16))
                    .apply(RequestOptions.centerCropTransform())
                    .into(holder.mDetailImgItem)
            }
            holder.mImageDescription.text =
                propertyImg.description
        }
    }

    override fun getItemCount(): Int {
        return mImageOfPropertyList!!.size
    }

    class DetailImageViewHolder(vBinding: ItemDetailImageBinding) :
        RecyclerView.ViewHolder(vBinding.root) {
        var mDetailImgItem: ImageView = vBinding.detailItemRecyclerImage
        var mImageDescription: TextView = vBinding.detailImgRecyclerViewDescription

    }
}