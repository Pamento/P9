package com.openclassrooms.realestatemanager.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
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
import com.openclassrooms.realestatemanager.data.remote.models.geocode.Location;
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
        if (isTwoFragmentLayout) {
            mDetailViewModel.getPropertyId();
        }
        setOnDataObservers();
    }

    private void setOnDataObservers() {
        mDetailViewModel.getSingleProperty().observe(getViewLifecycleOwner(), getProperty);
        mDetailViewModel.getImagesOfProperty().observe(getViewLifecycleOwner(), getImagesOfProperty);
    }

    private final Observer<List<ImageOfProperty>> getImagesOfProperty = imageOfProperties -> {
        if (imageOfProperties != null) {
            if (mImageOfPropertyList.size() > 0) mImageOfPropertyList.clear();
            mImageOfPropertyList.addAll(imageOfProperties);
            displayDataOnRecyclerView();
        }
    };

    private final Observer<SingleProperty> getProperty = new Observer<SingleProperty>() {
        @Override
        public void onChanged(SingleProperty singleProperty) {
            if (singleProperty != null) {
                mSingleProperty = singleProperty;
                if (mSingleProperty.getLocation().equals("")) {
                    getLocationFromAddress();
                } else {
                    mDetailViewModel.setUrlOfStaticMapOfProperty(singleProperty.getLocation());
                }
            }
            updateUI();
        }
    };

    private void unsubscribeImagesOfProperty() {
        mDetailViewModel.getImagesOfProperty().removeObserver(getImagesOfProperty);
    }

    private void unsubscribeGetProperty() {
        mDetailViewModel.getSingleProperty().removeObserver(getProperty);
    }

    private void getAllProperties() {
        mDetailViewModel.getAllProperties().observe(getViewLifecycleOwner(), getPropertyWithImages);
    }

    private final Observer<List<PropertyWithImages>> getPropertyWithImages = new Observer<List<PropertyWithImages>>() {
        @Override
        public void onChanged(List<PropertyWithImages> propertyWithImages) {
            if (propertyWithImages != null) {
                PropertyWithImages property;
                if (isFirstItemOfList) {
                    property = propertyWithImages.get(0);
                } else {
                    property = propertyWithImages.get(propertyWithImages.size() - 1);
                }
                mSingleProperty = property.mSingleProperty;
                if (property.mSingleProperty.getLocation().equals("") && Utils.isInternetAvailable(requireContext())) {
                    getLocationFromAddress();
                } else {
                    mDetailViewModel.setUrlOfStaticMapOfProperty(property.mSingleProperty.getLocation());
                }
                updateUI();
                if (mImageOfPropertyList.size() > 0) mImageOfPropertyList.clear();
                mImageOfPropertyList.addAll(property.ImagesOfProperty);
                displayDataOnRecyclerView();
            }
        }
    };

    private void unsubscribeGetProperties() {
        mDetailViewModel.getAllProperties().removeObserver(getPropertyWithImages);
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
        mDetailViewModel.getGeoLocationOfProperty().observe(getViewLifecycleOwner(), getLocation);
    }

    private void unsubscribeGetLocation() {
        mDetailViewModel.getGeoLocationOfProperty().removeObserver(getLocation);
    }

    final Observer<Location> getLocation = new Observer<Location>() {
        @Override
        public void onChanged(Location location) {
            if (location != null) {
                String l = String.valueOf(location.getLat()) + "," + String.valueOf(location.getLng());
                mDetailViewModel.setUrlOfStaticMapOfProperty(l);
                setStaticMapOfProperty();
                mSingleProperty.setLocation(l);
                updatePropertyInRoom();
                unsubscribeGetLocation();
            }
        }
    };

    private void setDetailRecyclerView() {
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

    private void updateUI() {
        if (mSingleProperty != null) {
            if (!mSingleProperty.getDateSold().equals(""))
                binding.detailAvailable.setText(R.string.detail_text_sold);
            else
                binding.detailAvailable.setText(R.string.detail_estate_available);
            // continue
            binding.detailDateRegister.setText(SQLTimeHelper.getUSFormDateFromTimeInMillis(Long.parseLong(mSingleProperty.getDateRegister())));
            String surfaceMetre = requireActivity().getResources().getString(R.string.detail_surface_integer);
            binding.detailSurface.setText(String.format(surfaceMetre, mSingleProperty.getSurface()));
            String estatePrice = mDetailViewModel.isDollar() ? requireActivity().getResources().getString(R.string.price_dollar) :
                    requireActivity().getResources().getString(R.string.price_euro);
            int price = mDetailViewModel.isDollar() ? mSingleProperty.getPrice() : Utils.convertDollarToEuro(mSingleProperty.getPrice());
            String priceComa = StringModifier.addComaInPrice(String.valueOf(price));
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

    public String getPropertyType() {
        if (mSingleProperty != null) return mSingleProperty.getType();
        else return "...";
    }

    public void handleCurrency(int oneIsDollar) {
        // Value of oneIsDollar = 1 -> $ (one is dollar)
        if (oneIsDollar == 2 && mDetailViewModel.isDollar()) mDetailViewModel.setDollar(false);
        else if (oneIsDollar == 1 && !mDetailViewModel.isDollar()) mDetailViewModel.setDollar(true);
        updateUI();
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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, amenities);
        binding.detailAmenitiesListView.setAdapter(adapter);
        AdapterHelper.setListViewHeightBasedOnChildren(binding.detailAmenitiesListView);
    }

    private void initViewModel() {
        ViewModelFactory vmF = Injection.sViewModelFactory(requireActivity());
        mDetailViewModel = new ViewModelProvider(this, vmF).get(DetailViewModel.class);
    }

    private void updatePropertyInRoom() {
        mDetailViewModel.updateSingleProperty(mSingleProperty);
    }

    @Override
    public void onDestroyView() {
        binding = null;
        mDetailViewModel.clearDisposable();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (mDetailViewModel.getGeoLocationOfProperty().hasActiveObservers()) unsubscribeGetLocation();
        if (mDetailViewModel.getAllProperties().hasActiveObservers()) unsubscribeGetProperties();
        if (mDetailViewModel.getSingleProperty().hasActiveObservers()) unsubscribeGetProperty();
        if (mDetailViewModel.getImagesOfProperty().hasActiveObservers()) unsubscribeImagesOfProperty();
        mDetailViewModel.disposeDisposable();
        super.onDestroy();
    }
}