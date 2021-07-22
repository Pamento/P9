package com.openclassrooms.realestatemanager.ui.adapters;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.data.local.entities.ImageOfProperty;
import com.openclassrooms.realestatemanager.databinding.AddImageItemBinding;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ImageListOfAddPropertyAdapter extends
        RecyclerView.Adapter<ImageListOfAddPropertyAdapter.ImageListOfAddPropertyViewHolder> {
    private static final String TAG = "AddProperty";
    private List<ImageOfProperty> mImageOfPropertyList;

    public ImageListOfAddPropertyAdapter(List<ImageOfProperty> imageOfPropertyList) {
        Log.i(TAG, "ADAPTER__ CONSTRUCTOR// ImageListOfAddPropertyAdapter: imageOfPropertyList.size():: " + imageOfPropertyList);
        mImageOfPropertyList = imageOfPropertyList;
    }

    @NonNull
    @Override
    public ImageListOfAddPropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AddImageItemBinding binding = AddImageItemBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ImageListOfAddPropertyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageListOfAddPropertyViewHolder holder, int position) {
        //Log.i(TAG, "ADAPTER__ onBindViewHolder: mImageOfPropertyList.size():: " + mImageOfPropertyList.size());
        if (mImageOfPropertyList.size() == 0) {
            holder.mImageDescription.setText(R.string.add_description_placeholder);
            Glide.with(holder.mImageProperty.getContext())
                    .load((Uri) null)
                    .placeholder(R.drawable.image_placeholder)
                    .into(holder.mImageProperty);
        } else {
            ImageOfProperty imgItem = mImageOfPropertyList.get(position);
            if (imgItem.getDescription() != null && !imgItem.getDescription().equals("")) {
                holder.mImageDescription.setText(imgItem.getDescription());
            }
            Uri uri = Uri.parse(imgItem.getPath());
            if (uri != null) {
                Glide.with(holder.mImageProperty.getContext())
                        .load(uri)
                        .error(R.drawable.image_not_found_square)
                        .placeholder(R.drawable.image_placeholder)
                        .transform(new RoundedCornersTransformation(20,1))
                        .into(holder.mImageProperty);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mImageOfPropertyList.size() == 0) {
            return 1;
        } else return mImageOfPropertyList.size();
    }

    public void updateImagesList(List<ImageOfProperty> images) {
        this.notifyDataSetChanged();
        this.mImageOfPropertyList.clear();
        //Log.i(TAG, "ADAPTER__ updateImagesList: images.size():: " + images.size());
        //Log.i(TAG, "ADAPTER__ updateImagesList: mImageOfProperty.size():: " + mImageOfPropertyList.size());
        this.mImageOfPropertyList.addAll(images);
        //Log.i(TAG, "ADAPTER__ updateImagesList: mImageOfProperty.size():: " + mImageOfPropertyList.size());
        this.notifyDataSetChanged();
    }

    public ImageOfProperty getImageOfPropertyAt(int position) {
        return mImageOfPropertyList.get(position);
    }

    public static class ImageListOfAddPropertyViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageProperty;
        EditText mImageDescription;


        public ImageListOfAddPropertyViewHolder(@NonNull AddImageItemBinding vBinding) {
            super(vBinding.getRoot());
            mImageProperty = vBinding.itemAddFImage;
            mImageDescription = vBinding.itemAddFInputDescription;
        }
    }
}
