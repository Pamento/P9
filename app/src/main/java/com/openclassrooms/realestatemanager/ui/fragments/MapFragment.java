package com.openclassrooms.realestatemanager.ui.fragments;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.sqlite.db.SimpleSQLiteQuery;

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
import com.openclassrooms.realestatemanager.util.enums.QueryState;
import com.openclassrooms.realestatemanager.util.notification.NotifyBySnackBar;
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
    public static final String FAB_DECIDER = "display_fab";
    private MapViewModel mMapViewModel;
    private View view;
    private FragmentMapBinding binding;
    private GoogleMap mGoogleMaps;
    private final List<PropertyWithImages> mPropertyWithImages = new ArrayList<>();
    private boolean isDoubleFragment = false;

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance(boolean isDoubleFragment) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putBoolean(FAB_DECIDER, isDoubleFragment);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isDoubleFragment = getArguments().getBoolean(FAB_DECIDER);
        }
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
        this.view = view;
        if (isDoubleFragment) {
            binding.fabList.setVisibility(View.GONE);
        } else {
            setFabListener();
        }
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
                    setQueryStateObserver();
                }
            }
        });
    }

    private void setQueryStateObserver() {
        Log.i(TAG, "MAP__ checkDataToDisplay: RUN");
        mMapViewModel.getQueryState().observe(getViewLifecycleOwner(), queryStateObserver);
    }

    private void unsubscribeQueryState() {
        mMapViewModel.getQueryState().removeObserver(queryStateObserver);
    }

    final Observer<QueryState> queryStateObserver = queryState -> {
        if (queryState.equals(QueryState.NULL)) {
            Log.i(TAG, "MAP__ checkDataToDisplay: IF:: all properties");
            setPropertyObserver();
        } else {
            setRowQueryObserver();
            Log.i(TAG, "MAP__ checkDataToDisplay: ELSE:: row query");
        }
    };

    private void setPropertiesMarkersOnMap() {
        if (mGoogleMaps == null) mGoogleMaps = mMapViewModel.getGoogleMap();
        if (mGoogleMaps != null) {
            mGoogleMaps.clear();

            if (mPropertyWithImages.size() != 0) {
                for (PropertyWithImages sp : mPropertyWithImages) {
                    Marker marker;
                    if (sp != null && !sp.mSingleProperty.getLocation().equals("")) {
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

    private void setPropertyObserver() {
        mMapViewModel.getPropertyWithImages().observe(getViewLifecycleOwner(), getPropertyWithImages);
    }

    private void unsubscribeProperty() {
        mMapViewModel.getPropertyWithImages().removeObserver(getPropertyWithImages);
    }

    private void setRowQueryObserver() {
        mMapViewModel.getSimpleSQLiteQuery().observe(getViewLifecycleOwner(), rowQueryObserver);
    }

    private void unsubscribeRowQuery() {
        mMapViewModel.getSimpleSQLiteQuery().removeObserver(rowQueryObserver);
    }

    public void resetRowQuery() {
        Log.i(TAG, "MAP__ resetRowQuery: ");
        mMapViewModel.setQueryState(QueryState.NULL);
        setQueryStateObserver();
    }

    final Observer<SimpleSQLiteQuery> rowQueryObserver = new Observer<SimpleSQLiteQuery>() {
        @Override
        public void onChanged(SimpleSQLiteQuery simpleSQLiteQuery) {
            if (simpleSQLiteQuery != null) {
                unsubscribeProperty();
                mPropertyWithImages.clear();
                List<PropertyWithImages> fromRowQuery = mMapViewModel.getPropertiesWithImagesFromRowQuery();
                if (fromRowQuery.size() == 0) {
                    String msg = getResources().getString(R.string.search_give_zero_data);
                    NotifyBySnackBar.showSnackBar(1, view, msg);
                }
                mPropertyWithImages.addAll(fromRowQuery);
                setPropertiesMarkersOnMap();
            }
        }
    };

    private final Observer<List<PropertyWithImages>> getPropertyWithImages =
            propertyWithImages -> {
                if (propertyWithImages != null) {
                    mPropertyWithImages.clear();
                    mPropertyWithImages.addAll(propertyWithImages);
                }
                setPropertiesMarkersOnMap();
            };

    private void getCurrentUserLocation() {
        Permissions.check(requireContext(), Constants.PERMISSIONS, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {
                if (LocationUtils.isDeviceLocationEnabled()) {
                    Objects.requireNonNull(LocationUtils.getCurrentDeviceLocation()).observe(getViewLifecycleOwner(), getLocation);
                } else {
                    String msg = "Please, switch ON location for use plenty of this app.";
                    NotifyBySnackBar.showSnackBar(1, view, msg);
                }
            }
        });
    }

    private final Observer<Location> getLocation = new Observer<Location>() {
        @Override
        public void onChanged(Location location) {
            if (location != null) {
                mMapViewModel.setCurrentUserLocation(location);
                if (mGoogleMaps != null) {
                    setMap();
                }
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
        if (fragment.equals(LIST)) ma.onBackPressed();
        else ma.displayFragm(fragment, toolbarTitle);
        Log.i(TAG, "MAP__ navigateToFragment: onBackPressed from MapFragment");
    }

    private String getPropertyType(String propertyId) {
        String pType = "";
        for (PropertyWithImages p : mPropertyWithImages) {
            if (p.mSingleProperty.getId().equals(propertyId))
                pType = p.mSingleProperty.getType();
        }
        return pType;
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "MAP__ onDestroy ");
        if (mMapViewModel.getQueryState().hasActiveObservers()) unsubscribeQueryState();
        if (mMapViewModel.getSimpleSQLiteQuery().hasActiveObservers())
            unsubscribeRowQuery();
        if (mMapViewModel.getPropertyWithImages().hasActiveObservers())
            unsubscribeProperty();
        super.onDestroy();
    }
}