package com.openclassrooms.realestatemanager.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.data.local.entities.ImageOfProperty;
import com.openclassrooms.realestatemanager.data.local.entities.SingleProperty;
import com.openclassrooms.realestatemanager.data.viewModelFactory.ViewModelFactory;
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.DetailViewModel;
import com.openclassrooms.realestatemanager.databinding.FragmentDetailBinding;
import com.openclassrooms.realestatemanager.injection.Injection;
import com.openclassrooms.realestatemanager.ui.adapters.ImageListOfDetailAdapter;
import com.openclassrooms.realestatemanager.util.dateTime.SQLTimeHelper;
import com.openclassrooms.realestatemanager.util.texts.StringModifier;

import java.util.ArrayList;
import java.util.List;

public class DetailFragment extends Fragment {
    private static final String TAG = "DetailFragment";
    private DetailViewModel mDetailViewModel;
    private FragmentDetailBinding binding;
    private SingleProperty mSingleProperty;
    private final List<ImageOfProperty> mImageOfPropertyList = new ArrayList<>();

    public DetailFragment() {
        // Required empty public constructor
    }

    public static DetailFragment newInstance() {
        return new DetailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initViewModel();
        // Inflate the layout for this fragment
        binding = FragmentDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setOnDataObservers();
        setUI();
    }

    private void setOnDataObservers() {
        mDetailViewModel.getSingleProperty().observe(getViewLifecycleOwner(), singleProperty -> mSingleProperty = singleProperty);
        mDetailViewModel.getImagesOfProperty().observe(getViewLifecycleOwner(), imagesOfProperty -> {
            mImageOfPropertyList.addAll(imagesOfProperty);
            // TODO if mImageOfPropertyList.size() > 1 -> setRecyclerView. else set ImageView
            setRecyclerView();
        });
    }

    private void setRecyclerView() {
        RecyclerView recyclerV = binding.detailImgRecyclerView;
        ImageListOfDetailAdapter adapter = new ImageListOfDetailAdapter(mImageOfPropertyList);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerV.setAdapter(adapter);
        recyclerV.setLayoutManager(layoutManager);
    }

    private void setUI() {
        if (mSingleProperty != null) {
            if (mSingleProperty.getDateSold() != null)
                binding.detailAvailable.setText(R.string.detail_text_sold);
            else
                binding.detailAvailable.setText(R.string.detail_estate_available);
            // continue
            binding.detailDateRegister.setText(SQLTimeHelper.getUSFormDateFromTimeInMillis(mSingleProperty.getDateRegister()));
            String surfaceMetre = requireActivity().getResources().getString(R.string.detail_surface_integer, mSingleProperty.getSurface());
            binding.detailSurface.setText(surfaceMetre);
            String estatePrice = requireActivity().getResources().getString(R.string.price_dollar, String.valueOf(mSingleProperty.getPrice()));
            binding.detailPrice.setText(estatePrice);
            String estateRooms = requireActivity().getResources().getString(R.string.detail_rooms, mSingleProperty.getRooms());
            binding.detailRoomsNumber.setText(estateRooms);
            String estateBathroom = requireActivity().getResources().getString(R.string.price_dollar, String.valueOf(mSingleProperty.getBathroom()));
            binding.detailBathroom.setText(estateBathroom);
            binding.detailDescription.setText(mSingleProperty.getDescription());
            // Address form
            binding.detailAddress1.setText(mSingleProperty.getAddress1());
            binding.detailAddress2.setText(mSingleProperty.getAddress2());
            binding.detailAddress3.setText(mSingleProperty.getQuarter());
            binding.detailAddress4.setText(mSingleProperty.getCity());
            binding.detailAddress5.setText(mSingleProperty.getPostalCode());
            String country = requireActivity().getResources().getString(R.string.detail_united_states);
            binding.detailAddress6.setText(country);
            setAmenitiesView();
            binding.detailAgent.setText(mSingleProperty.getAgent());
        }
    }

    private void setAmenitiesView() {
        String[] amenities = StringModifier.singleStringToArrayString(mSingleProperty.getAmenities());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_dropdown_item_1line, amenities);
        binding.detailAmenitiesListView.setAdapter(adapter);
    }

    private void initViewModel() {
        ViewModelFactory vmF = Injection.sViewModelFactory(requireActivity());
        mDetailViewModel = new ViewModelProvider(requireActivity(), vmF).get(DetailViewModel.class);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}