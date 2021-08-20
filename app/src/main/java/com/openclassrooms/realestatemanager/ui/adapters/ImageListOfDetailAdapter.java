package com.openclassrooms.realestatemanager.ui.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.data.local.entities.ImageOfProperty;
import com.openclassrooms.realestatemanager.databinding.ItemDetailImageBinding;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ImageListOfDetailAdapter extends RecyclerView.Adapter<ImageListOfDetailAdapter.DetailImageViewHolder> {

    private final List<ImageOfProperty> mImageOfPropertyList;

    public ImageListOfDetailAdapter(List<ImageOfProperty> imageOfPropertyList) {
        mImageOfPropertyList = imageOfPropertyList;
    }

    @NonNull
    @Override
    public DetailImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDetailImageBinding binding = ItemDetailImageBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new DetailImageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailImageViewHolder holder, int position) {
        if (mImageOfPropertyList != null ) {
            ImageOfProperty propertyImg = mImageOfPropertyList.get(position);
            Uri uri = Uri.parse(propertyImg.getPath());
            if (uri != null) {
                Glide.with(holder.mDetailImgItem.getContext())
                        .load(uri)
                        .error(R.drawable.image_not_found_square)
                        .placeholder(R.drawable.image_placeholder)
                        .transform(new RoundedCornersTransformation(20, 16))
                        .apply(RequestOptions.centerCropTransform())
                        .into(holder.mDetailImgItem);
            }
            if (propertyImg.getDescription() != null)
                holder.mImageDescription.setText(propertyImg.getDescription());
        }
    }

    @Override
    public int getItemCount() {
        return mImageOfPropertyList.size();
    }

    public static class DetailImageViewHolder extends RecyclerView.ViewHolder {
        ImageView mDetailImgItem;
        TextView mImageDescription;

        public DetailImageViewHolder(@NonNull ItemDetailImageBinding vBinding) {
            super(vBinding.getRoot());
            mDetailImgItem = vBinding.detailItemRecyclerImage;
            mImageDescription = vBinding.detailImgRecyclerViewDescription;
        }
    }
}
