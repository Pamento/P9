package com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.GoogleMap;
import com.openclassrooms.realestatemanager.data.local.entities.PropertyWithImages;
import com.openclassrooms.realestatemanager.data.local.reposiotries.ImageRepository;
import com.openclassrooms.realestatemanager.data.local.reposiotries.PropertiesRepository;

import java.util.List;

public class MapViewModel extends ViewModel {

    private final PropertiesRepository mPropertiesRepository;
    private final ImageRepository mImageRepository;
    private GoogleMap mGoogleMap;
    private Location mCurrentUserLocation;

    public MapViewModel(PropertiesRepository propertiesRepository, ImageRepository imageRepository) {
        mPropertiesRepository = propertiesRepository;
        mImageRepository = imageRepository;
    }

    public LiveData<List<PropertyWithImages>> getPropertyWithImages() {
        return mPropertiesRepository.getAllPropertiesWithImages();
    }

    public void setPropertyId(String propertyId) {
        mPropertiesRepository.setPROPERTY_ID(propertyId);
        mImageRepository.setPropertyId(propertyId);
    }

    public GoogleMap getGoogleMap() {
        return mGoogleMap;
    }

    public void setGoogleMap(GoogleMap googleMap) {
        mGoogleMap = googleMap;
    }

    // CurrentLocation
    public Location getCurrentUserLocation() {
        return mCurrentUserLocation;
    }

    public void setCurrentUserLocation(Location currentUserLocation) {
        mCurrentUserLocation = currentUserLocation;
    }
}
