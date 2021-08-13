package com.openclassrooms.realestatemanager.ui.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.data.local.entities.PropertyWithImages;
import com.openclassrooms.realestatemanager.databinding.ListItemBinding;
import com.openclassrooms.realestatemanager.util.texts.StringModifier;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ListPropertyAdapter extends RecyclerView.Adapter<ListPropertyAdapter.ListPropertyViewHolder> {
    private final List<PropertyWithImages> mProperties;
    private final OnItemPropertyListClickListener mListClickListener;

    public ListPropertyAdapter(List<PropertyWithImages> properties, OnItemPropertyListClickListener listClickListener) {
        mProperties = properties;
        mListClickListener = listClickListener;
    }

    @NonNull
    @Override
    public ListPropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemBinding binding = ListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ListPropertyViewHolder(binding, mListClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ListPropertyViewHolder holder, int position) {
        if (mProperties.size() > 0) {
            PropertyWithImages property = mProperties.get(position);
            String price = StringModifier.addComaInPrice(String.valueOf(property.mSingleProperty.getPrice()));
            String priceDollar = holder.itemView.getContext().getString(R.string.price_dollar);
            Uri uri = null;
            if (property.ImagesOfProperty.size() > 0) {
                uri = Uri.parse(property.ImagesOfProperty.get(0).getPath());
            }
            holder.type.setText(property.mSingleProperty.getType());
            holder.address.setText(property.mSingleProperty.getQuarter());
            holder.price.setText(String.format(priceDollar, price));
            Glide.with(holder.image.getContext())
                    .load(uri)
                    .error(R.drawable.image_not_found_square)
                    .placeholder(R.drawable.image_placeholder)
                    .transform(new RoundedCornersTransformation(12, 1))
                    .apply(RequestOptions.centerCropTransform())
                    .into(holder.image);
        } else {
            holder.type.setText(R.string.list_empty_item_type);
            holder.address.setText(R.string.list_empty_item_quarter);
            holder.price.setText(R.string.list_empty_item_price);
            Glide.with(holder.image.getContext())
                    .load(R.drawable.image_placeholder)
                    .into(holder.image);
        }

    }

    @Override
    public int getItemCount() {
        if (mProperties.size() > 0)
            return mProperties.size();
        else return 1;
    }

    public static class ListPropertyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView image;
        TextView type;
        TextView address;
        TextView price;
        OnItemPropertyListClickListener mOnItemPropertyListClickListener;

        public ListPropertyViewHolder(@NonNull ListItemBinding vBinding, OnItemPropertyListClickListener onItemPropertyListClickListener) {
            super(vBinding.getRoot());
            image = vBinding.listImg;
            type = vBinding.listType;
            address = vBinding.listAddress;
            price = vBinding.listPrice;
            mOnItemPropertyListClickListener = onItemPropertyListClickListener;
            vBinding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnItemPropertyListClickListener.onItemPropertyListClickListener(getAbsoluteAdapterPosition());
        }
    }

    public interface OnItemPropertyListClickListener {
        void onItemPropertyListClickListener(int position);
    }
}
