package com.openclassrooms.realestatemanager.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.bumptech.glide.Glide;
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
    private RecyclerView recyclerV;
    private ImageListOfDetailAdapter mAdapter;
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
        setDetailRecyclerView();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setOnDataObservers();
    }

    private void setOnDataObservers() {
        Log.i(TAG, "DETAIL__ setOnDataObservers: run");
        mDetailViewModel.getSingleProperty().observe(getViewLifecycleOwner(), singleProperty -> {
            if (singleProperty != null) {
                mSingleProperty = singleProperty;
                mDetailViewModel.setUrlOfStaticMapOfProperty(singleProperty.getLocation());
            }
            setUI();
        });
        mDetailViewModel.getImagesOfProperty().observe(getViewLifecycleOwner(), imagesOfProperty -> {
            mImageOfPropertyList.addAll(imagesOfProperty);
            // TODO if mImageOfPropertyList.size() > 1 -> setDetailRecyclerView. else set ImageView
            displayDataOnRecyclerView();
        });
    }

    private void setDetailRecyclerView() {
        Log.i(TAG, "DETAIL__ setDetailRecyclerView: adapter _____________adapter");
        recyclerV = binding.detailImgRecyclerView;
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerV.setHasFixedSize(true);
        recyclerV.setLayoutManager(layoutManager);
    }

    private void displayDataOnRecyclerView() {
        mAdapter = new ImageListOfDetailAdapter(mImageOfPropertyList);
        recyclerV.setAdapter(mAdapter);
    }

    private void setUI() {
        if (mSingleProperty != null) {
            Log.i(TAG, "DETAIL__ setUI: ");
            if (!mSingleProperty.getDateSold().equals(""))
                binding.detailAvailable.setText(R.string.detail_text_sold);
            else
                binding.detailAvailable.setText(R.string.detail_estate_available);
            // continue
            Log.i(TAG, "setUI: time::: " + mSingleProperty.getDateRegister());
            binding.detailDateRegister.setText(SQLTimeHelper.getUSFormDateFromTimeInMillis(Long.parseLong(mSingleProperty.getDateRegister())));
            String surfaceMetre = requireActivity().getResources().getString(R.string.detail_surface_integer);
            binding.detailSurface.setText(String.format(surfaceMetre, mSingleProperty.getSurface()));
            String estatePrice = requireActivity().getResources().getString(R.string.price_dollar);
            String priceComa = StringModifier.addComaInPrice(String.valueOf(mSingleProperty.getPrice()));
            binding.detailPrice.setText(String.format(estatePrice, priceComa));
            String estateRooms = requireActivity().getResources().getString(R.string.detail_rooms);
            binding.detailRoomsNumber.setText(String.format(estateRooms, mSingleProperty.getRooms()));
            String estateBathroom = requireActivity().getResources().getString(R.string.detail_bathroom);
            binding.detailBathroomsNumber.setText(String.format(estateBathroom,mSingleProperty.getBathroom()));
            String estateBedroom = requireActivity().getResources().getString(R.string.detail_bedroom);
            binding.detailBedroomNumber.setText(String.format(estateBedroom, mSingleProperty.getBedroom()));
            binding.detailDescription.setText(mSingleProperty.getDescription());
            // Address form
            binding.detailAddress1.setText(mSingleProperty.getAddress1());
            binding.detailAddress2.setText(mSingleProperty.getAddress2());
            binding.detailAddressQuarter.setText(mSingleProperty.getQuarter());
            binding.detailAddressCity.setText(mSingleProperty.getCity());
            binding.detailAddressPostalCode.setText(String.valueOf(mSingleProperty.getPostalCode()));
            String country = requireActivity().getResources().getString(R.string.detail_united_states);
            binding.detailAddress6.setText(country);
            setStaticMapOfProperty();
            setAmenitiesView();
            binding.detailAgent.setText(mSingleProperty.getAgent());
        }
    }

    private void setStaticMapOfProperty() {
        Log.i(TAG, "DETAIL__ setStaticMapOfProperty: GLIDE run");
        Glide.with(requireContext())
                .load(mDetailViewModel.getUrlOfStaticMapOfProperty())
                .error(R.drawable.image_not_found_square)
                .into(binding.detailSmallStaticMap);
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