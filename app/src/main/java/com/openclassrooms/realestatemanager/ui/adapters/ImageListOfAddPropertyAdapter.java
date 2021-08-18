package com.openclassrooms.realestatemanager.ui.adapters;

import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.data.local.entities.ImageOfProperty;
import com.openclassrooms.realestatemanager.databinding.AddImageItemBinding;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ImageListOfAddPropertyAdapter extends
        RecyclerView.Adapter<ImageListOfAddPropertyAdapter.ImageListOfAddPropertyViewHolder> {
    private static final String TAG = "AddProperty";
    private final List<ImageOfProperty> mImageOfPropertyList;

    public ImageListOfAddPropertyAdapter(List<ImageOfProperty> imageOfPropertyList) {
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
        holder.mImageDescription.removeTextChangedListener(holder.mTextWatcher);
        if (mImageOfPropertyList.size() == 0) {
            holder.mImageDescription.setText(R.string.add_description_placeholder);
            Glide.with(holder.mImageProperty.getContext())
                    .load((Uri) null)
                    .placeholder(R.drawable.image_placeholder)
                    .into(holder.mImageProperty);
        } else {
            ImageOfProperty imgItem = mImageOfPropertyList.get(position);
            Uri uri = Uri.parse(imgItem.getPath());
            if (uri != null) {
                Glide.with(holder.mImageProperty.getContext())
                        .load(uri)
                        .error(R.drawable.image_not_found_square)
                        .placeholder(R.drawable.image_placeholder)
                        .transform(new RoundedCornersTransformation(20,1))
                        .into(holder.mImageProperty);
            }
            if (imgItem.getDescription() != null && !imgItem.getDescription().equals("")) {
                holder.mImageDescription.setText(imgItem.getDescription());
            }

            holder.mImageDescription.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {/**/}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {/**/}

                @Override
                public void afterTextChanged(Editable editable) {
                    ImageOfProperty iop = mImageOfPropertyList.get(position);
                    iop.setDescription(editable.toString());
                    mImageOfPropertyList.set(position, iop);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mImageOfPropertyList.size() == 0) {
            return 1;
        } else return mImageOfPropertyList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void updateImagesList(List<ImageOfProperty> images) {
        this.notifyDataSetChanged();
        this.mImageOfPropertyList.clear();
        this.mImageOfPropertyList.addAll(images);
        this.notifyDataSetChanged();
    }

    public ImageOfProperty getImageOfPropertyAt(int position) {
        return mImageOfPropertyList.get(position);
    }

    public static class ImageListOfAddPropertyViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageProperty;
        TextInputEditText mImageDescription;
        TextWatcher mTextWatcher = null;


        public ImageListOfAddPropertyViewHolder(@NonNull AddImageItemBinding vBinding) {
            super(vBinding.getRoot());
            mImageProperty = vBinding.itemAddFImage;
            mImageDescription = vBinding.itemAddFInputDescription;
        }
    }

    public List<ImageOfProperty> getImageOfPropertyList() {
        return mImageOfPropertyList;
    }

    public void addNewImage(ImageOfProperty imageOfProperty) {
        this.mImageOfPropertyList.add(imageOfProperty);
        this.notifyItemInserted(mImageOfPropertyList.size());
    }

    public void removeDeletedImageFromList(int imageOfProperty) {
        mImageOfPropertyList.remove(imageOfProperty);
        this.notifyItemRemoved(imageOfProperty);
        this.notifyItemRangeChanged(imageOfProperty, mImageOfPropertyList.size());
    }
}
