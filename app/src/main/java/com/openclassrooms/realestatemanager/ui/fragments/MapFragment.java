package com.openclassrooms.realestatemanager.ui.fragments;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.data.local.entities.PropertyWithImages;
import com.openclassrooms.realestatemanager.data.viewModelFactory.ViewModelFactory;
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.MapViewModel;
import com.openclassrooms.realestatemanager.databinding.FragmentMapBinding;
import com.openclassrooms.realestatemanager.injection.Injection;
import com.openclassrooms.realestatemanager.ui.activity.MainActivity;
import com.openclassrooms.realestatemanager.util.Constants.Constants;
import com.openclassrooms.realestatemanager.util.Utils;
import com.openclassrooms.realestatemanager.util.enums.EFragments;
import com.openclassrooms.realestatemanager.util.system.LocationUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.openclassrooms.realestatemanager.util.enums.EFragments.DETAIL;
import static com.openclassrooms.realestatemanager.util.enums.EFragments.LIST;

public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {

    private static final String TAG = "MapFragment";
    private MapViewModel mMapViewModel;
    private FragmentMapBinding binding;
    private GoogleMap mGoogleMaps;
    private List<PropertyWithImages> mPropertyWithImages = new ArrayList<>();

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance(String param1, String param2) {
        return new MapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initViewModel();
        binding = FragmentMapBinding.inflate(inflater, container, false);
        initMap();
        getCurrentUserLocation();
        return binding.getRoot();
    }

    private void initViewModel() {
        ViewModelFactory vmF = Injection.sViewModelFactory(requireActivity());
        mMapViewModel = new ViewModelProvider(requireActivity(), vmF).get(MapViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setFabListener();
    }

    private void initMap() {
        if (Utils.isInternetAvailable(requireContext())) {
            SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            if (supportMapFragment != null) {
                supportMapFragment.getMapAsync(MapFragment.this);
            }
        }
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        if (marker.getTag() != null) {
            Log.i(TAG, "onMarkerClick: tag:: " + marker.getTag().toString());
            Log.i(TAG, "onMarkerClick: propertyId:: " + mPropertyWithImages.get(0).mSingleProperty.getId());
            Log.i(TAG, "onMarkerClick: type:: " + getPropertyType(marker.getTag().toString()));
            mMapViewModel.setPropertyId(marker.getTag().toString());
            navigateToFragment(DETAIL, getPropertyType(marker.getTag().toString()));
        }
        return false;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.mGoogleMaps = googleMap;
        mGoogleMaps.setOnMarkerClickListener(this);
        mMapViewModel.setGoogleMap(googleMap);
        Permissions.check(requireContext(), Constants.PERMISSIONS, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {
                Log.i(TAG, "MVF__ onGranted: PERMISSIONS");
                setMap();
            }
        });
    }

    private void setMap() {
        Permissions.check(requireContext(), Constants.PERMISSIONS, null, null, new PermissionHandler() {
            @SuppressLint("MissingPermission")
            @Override
            public void onGranted() {
                if (mMapViewModel.getCurrentUserLocation() != null) {
                    moveCamera(mMapViewModel.getCurrentUserLocation());
                    // display point bleu on the map
                    mGoogleMaps.setMyLocationEnabled(true);
                    setPropertyWithImages();
                }
            }
        });
    }

    private void setPropertiesMarkersOnMap() {
        if (mGoogleMaps == null) mGoogleMaps = mMapViewModel.getGoogleMap();
        if (mGoogleMaps != null) {
            mGoogleMaps.clear();

            if (mPropertyWithImages.size() != 0) {
                for (PropertyWithImages sp : mPropertyWithImages) {
                    Marker marker;
                    if (sp != null) {
                        String[] latLang = sp.mSingleProperty.getLocation().split(",");
                        Log.i(TAG, "setPropertiesMarkersOnMap: LOCATION:: " + Arrays.toString(latLang));
                        LatLng latLng = new LatLng(Double.parseDouble(latLang[0]), Double.parseDouble(latLang[1]));
                        marker = mGoogleMaps.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(sp.mSingleProperty.getAddress1()));

                        if (marker != null) marker.setTag(sp.mSingleProperty.getId());
                    }
                }
            }
        }
    }

    private void setPropertyWithImages() {
        if (mMapViewModel.getSimpleSQLiteQuery() != null) {
            mPropertyWithImages = mMapViewModel.getPropertiesWithImagesFromRowQuery();
            if (mPropertyWithImages != null) {
                setPropertiesMarkersOnMap();
            }
        } else {
            mMapViewModel.getPropertyWithImages().observe(getViewLifecycleOwner(), getPropertyWithImages);
        }
    }

    private final Observer<List<PropertyWithImages>> getPropertyWithImages =
            propertyWithImages -> {
                if (propertyWithImages != null) mPropertyWithImages = propertyWithImages;
                setPropertiesMarkersOnMap();
            };

    private void getCurrentUserLocation() {
        Permissions.check(requireContext(), Constants.PERMISSIONS, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {
                if (LocationUtils.isDeviceLocationEnabled()) {
                    Objects.requireNonNull(LocationUtils.getCurrentDeviceLocation()).observe(getViewLifecycleOwner(), getLocation);
                } else {
                    // TODO display message
                }
            }
        });
    }

    private final Observer<Location> getLocation = new Observer<Location>() {
        @Override
        public void onChanged(Location location) {
            if (location != null) {
                mMapViewModel.setCurrentUserLocation(location);
                // TODO update user position on map
            }
        }
    };

    private void moveCamera(Location loc) {
        LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
        mGoogleMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, Constants.DEFAULT_MAPS_ZOOM));
    }

    private void setFabListener() {
        binding.fabList.setOnClickListener(view -> {
            Log.i(TAG, "onClick: LIST _ fab;;");
            navigateToFragment(LIST, "");
        });
    }

    private void navigateToFragment(EFragments fragment, String toolbarTitle) {
        MainActivity ma = (MainActivity) requireActivity();
        ma.displayFragm(fragment, toolbarTitle);
    }

    private String getPropertyType(String propertyId) {
        String pType = "";
        for (PropertyWithImages p : mPropertyWithImages) {
            if (p.mSingleProperty.getId().equals(propertyId)) pType = p.mSingleProperty.getType();
        }
        return pType;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}