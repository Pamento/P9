package com.openclassrooms.realestatemanager.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassrooms.realestatemanager.data.model.SingleProperty;
import com.openclassrooms.realestatemanager.databinding.ListItemBinding;

import java.util.List;

public class ListPropertyAdapter extends RecyclerView.Adapter<ListPropertyAdapter.ListPropertyViewHolder> {

    private final List<SingleProperty> mProperties;
    private final OnItemPropertyListClickListener mListClickListener;

    public ListPropertyAdapter(List<SingleProperty> properties, OnItemPropertyListClickListener listClickListener) {
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
        holder.type.setText("HOUSE");
        holder.address.setText("147 avenue Adams Family");
        holder.price.setText("1,000,000 $");

    }

    @Override
    public int getItemCount() {
        //return mProperties.size();
        return 3;
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
