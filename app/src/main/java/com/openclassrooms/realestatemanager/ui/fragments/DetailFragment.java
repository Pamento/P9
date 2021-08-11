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
import com.openclassrooms.realestatemanager.data.local.entities.*;
import com.openclassrooms.realestatemanager.data.viewModelFactory.ViewModelFactory;
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.DetailViewModel;
import com.openclassrooms.realestatemanager.databinding.FragmentDetailBinding;
import com.openclassrooms.realestatemanager.injection.Injection;
import com.openclassrooms.realestatemanager.ui.adapters.ImageListOfDetailAdapter;
import com.openclassrooms.realestatemanager.util.Utils;
import com.openclassrooms.realestatemanager.util.dateTime.SQLTimeHelper;
import com.openclassrooms.realestatemanager.util.system.AdapterHelper;
import com.openclassrooms.realestatemanager.util.texts.StringModifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class DetailFragment extends Fragment {
    private static final String TAG = "DetailFragment";
    private static final String LAYOUT_MODE = "double";
    private static final String POSITION_OF_LIST = "position_list";
    private boolean isTwoFragmentLayout = false;
    private boolean isFirstItemOfList = true;
    private DetailViewModel mDetailViewModel;
    private FragmentDetailBinding binding;
    private SingleProperty mSingleProperty;
    private RecyclerView recyclerV;
    private final List<ImageOfProperty> mImageOfPropertyList = new ArrayList<>();

    public DetailFragment() {
        // Required empty public constructor
    }

    public static DetailFragment newInstance(boolean isTwoFragmentsToDisplay, boolean displayFirstItem) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putBoolean(LAYOUT_MODE, isTwoFragmentsToDisplay);
        args.putBoolean(POSITION_OF_LIST, displayFirstItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isTwoFragmentLayout = getArguments().getBoolean(LAYOUT_MODE);
            isFirstItemOfList = getArguments().getBoolean(POSITION_OF_LIST);
        }
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
        Log.i(TAG, "DETAIL__ onViewCreated: isTwoFragments:: " + isTwoFragmentLayout);
        Log.i(TAG, "DETAIL__ onViewCreated: isTwoFragments::id " + mDetailViewModel.getPropertyId());
        if (isTwoFragmentLayout && mDetailViewModel.getPropertyId() == null) {
            getAllProperties();
        } else {
            setOnDataObservers();
        }
    }

    private void setOnDataObservers() {
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

    private void getAllProperties() {
        Log.i(TAG, "DETAIL__ getAllProperties: run");
        mDetailViewModel.getAllProperties().observe(getViewLifecycleOwner(), propertiesWithImages -> {
            if (propertiesWithImages != null) {
                PropertyWithImages property;
                Log.i(TAG, "DETAIL__ getAllProperties: size:: " + propertiesWithImages.size());
                if (isFirstItemOfList) {
                    property = propertiesWithImages.get(0);
                } else {
                    property = propertiesWithImages.get(propertiesWithImages.size() - 1);
                }
                mSingleProperty = property.mSingleProperty;
                // TODO is network is available
                if (property.mSingleProperty.getLocation().equals("")) {
                    getLocationFromAddress();
                } else {
                    mDetailViewModel.setUrlOfStaticMapOfProperty(property.mSingleProperty.getLocation());
                }
                setUI();
                mImageOfPropertyList.addAll(property.ImagesOfProperty);
                displayDataOnRecyclerView();
            }
        });
    }

    private void getLocationFromAddress() {
        if (Utils.isInternetAvailable(requireContext())) {
            String address1 = mSingleProperty.getAddress1();
            String city = mSingleProperty.getCity();
            String quarter = mSingleProperty.getQuarter();
            String address = StringModifier.formatAddressToGeocoding(address1, city, quarter);
            mDetailViewModel.getLocationFromAddress(address);
            setResponseObserver();
        }
    }

    private void setResponseObserver() {
        mDetailViewModel.getGeoLocationOfProperty().observe(getViewLifecycleOwner(), location -> {
            if (location != null) {
                String l = String.valueOf(location.getLat()) + "," + String.valueOf(location.getLng());
                mDetailViewModel.setUrlOfStaticMapOfProperty(l);
            }
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
        ImageListOfDetailAdapter adapter = new ImageListOfDetailAdapter(mImageOfPropertyList);
        recyclerV.setAdapter(adapter);
    }

    private void setUI() {
        if (mSingleProperty != null) {
            Log.i(TAG, "DETAIL__ setUI: ");
            if (!mSingleProperty.getDateSold().equals(""))
                binding.detailAvailable.setText(R.string.detail_text_sold);
            else
                binding.detailAvailable.setText(R.string.detail_estate_available);
            // continue
            Log.i(TAG, "DETAIL__ setUI: time::: " + mSingleProperty.getDateRegister());
            binding.detailDateRegister.setText(SQLTimeHelper.getUSFormDateFromTimeInMillis(Long.parseLong(mSingleProperty.getDateRegister())));
            String surfaceMetre = requireActivity().getResources().getString(R.string.detail_surface_integer);
            binding.detailSurface.setText(String.format(surfaceMetre, mSingleProperty.getSurface()));
            String estatePrice = requireActivity().getResources().getString(R.string.price_dollar);
            String priceComa = StringModifier.addComaInPrice(String.valueOf(mSingleProperty.getPrice()));
            binding.detailPrice.setText(String.format(estatePrice, priceComa));
            String estateRooms = requireActivity().getResources().getString(R.string.detail_rooms);
            binding.detailRoomsNumber.setText(String.format(estateRooms, mSingleProperty.getRooms()));
            String estateBathroom = requireActivity().getResources().getString(R.string.detail_bathroom);
            binding.detailBathroomsNumber.setText(String.format(estateBathroom, mSingleProperty.getBathroom()));
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
        Glide.with(requireContext())
                .load(mDetailViewModel.getUrlOfStaticMapOfProperty())
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_not_found_square)
                .transform(new RoundedCornersTransformation(2, 2))
                .into(binding.detailSmallStaticMap);
    }

    private void setAmenitiesView() {
        String[] amenities = StringModifier.singleStringToArrayString(mSingleProperty.getAmenities());
        Log.i(TAG, "setAmenitiesView: DETAIL:: " + Arrays.toString(amenities));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, amenities);
        binding.detailAmenitiesListView.setAdapter(adapter);
        AdapterHelper.setListViewHeightBasedOnChildren(binding.detailAmenitiesListView);
    }

    private void initViewModel() {
        ViewModelFactory vmF = Injection.sViewModelFactory(requireActivity());
        mDetailViewModel = new ViewModelProvider(requireActivity(), vmF).get(DetailViewModel.class);
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}