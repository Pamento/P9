package com.openclassrooms.realestatemanager.ui.adapters

import android.net.Uri
import com.openclassrooms.realestatemanager.data.local.entities.PropertyWithImages
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.ui.adapters.ListPropertyAdapter.ListPropertyViewHolder
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.openclassrooms.realestatemanager.util.texts.StringModifier
import com.openclassrooms.realestatemanager.R
import com.bumptech.glide.Glide
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import com.bumptech.glide.request.RequestOptions
import android.widget.TextView
import com.openclassrooms.realestatemanager.databinding.ListItemBinding

class ListPropertyAdapter(
    private val mProperties: List<PropertyWithImages>,
    private val mListClickListener: OnItemPropertyListClickListener
) : RecyclerView.Adapter<ListPropertyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListPropertyViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListPropertyViewHolder(binding, mListClickListener)
    }

    override fun onBindViewHolder(holder: ListPropertyViewHolder, position: Int) {
        if (mProperties.isNotEmpty()) {
            val property = mProperties[position]
            val price = StringModifier.addComaInPrice(property.mSingleProperty!!.price.toString())
            val priceDollar = holder.itemView.context.getString(R.string.price_dollar)
            var uri: Uri? = null
            if (property.ImagesOfProperty!!.isNotEmpty()) {
                uri = Uri.parse(property.ImagesOfProperty!![0].path)
            }
            holder.type.text = property.mSingleProperty!!.type
            holder.address.text = property.mSingleProperty!!.quarter
            holder.price.text = String.format(priceDollar, price)
            Glide.with(holder.image.context)
                .load(uri)
                .error(R.drawable.image_not_found_square)
                .placeholder(R.drawable.image_placeholder)
                .transform(RoundedCornersTransformation(12, 1))
                .apply(RequestOptions.centerCropTransform())
                .into(holder.image)
        } else {
            holder.type.setText(R.string.list_empty_item_type)
            holder.address.setText(R.string.list_empty_item_quarter)
            holder.price.setText(R.string.list_empty_item_price)
            Glide.with(holder.image.context)
                .load(R.drawable.image_placeholder)
                .into(holder.image)
        }
    }

    override fun getItemCount(): Int {
        return if (mProperties.isNotEmpty()) mProperties.size else 1
    }

    class ListPropertyViewHolder(
        vBinding: ListItemBinding,
        onItemPropertyListClickListener: OnItemPropertyListClickListener
    ) : RecyclerView.ViewHolder(vBinding.root), View.OnClickListener {
        var image: ImageView = vBinding.listImg
        var type: TextView = vBinding.listType
        var address: TextView = vBinding.listAddress
        var price: TextView = vBinding.listPrice
        private var mOnItemPropertyListClickListener: OnItemPropertyListClickListener =
            onItemPropertyListClickListener

        override fun onClick(view: View) {
            mOnItemPropertyListClickListener.onItemPropertyListClickListener(absoluteAdapterPosition)
        }

        init {
            vBinding.root.setOnClickListener(this)
        }
    }

    interface OnItemPropertyListClickListener {
        fun onItemPropertyListClickListener(position: Int)
    }
}