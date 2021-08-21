package com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.google.android.gms.maps.GoogleMap;
import com.openclassrooms.realestatemanager.data.local.entities.PropertyWithImages;
import com.openclassrooms.realestatemanager.data.local.reposiotries.ImageRepository;
import com.openclassrooms.realestatemanager.data.local.reposiotries.PropertiesRepository;
import com.openclassrooms.realestatemanager.util.enums.QueryState;

import java.util.List;
import java.util.concurrent.Executor;

public class MapViewModel extends ViewModel {

    private final PropertiesRepository mPropertiesRepository;
    private final ImageRepository mImageRepository;
    private final Executor mExecutor;
    private GoogleMap mGoogleMap;
    private Location mCurrentUserLocation;
    private final MutableLiveData<List<PropertyWithImages>> mPropertyWithImageQuery = new MutableLiveData<>();

    public MapViewModel(PropertiesRepository propertiesRepository, ImageRepository imageRepository, Executor executor) {
        mPropertiesRepository = propertiesRepository;
        mImageRepository = imageRepository;
        mExecutor = executor;
    }

    public void getPropertiesWithImagesFromRowQuery() {
        mExecutor.execute(() ->
            mPropertyWithImageQuery.postValue(mPropertiesRepository.getPropertiesWithImagesQuery())
        );
    }

    public LiveData<List<PropertyWithImages>> getPropertyWithImages() {
        return mPropertiesRepository.getAllPropertiesWithImages();
    }

    public LiveData<SimpleSQLiteQuery> getSimpleSQLiteQuery() {
        return mPropertiesRepository.getRowQueryProperties();
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

    public LiveData<QueryState> getQueryState() {
        return mPropertiesRepository.getQueryState();
    }

    public void setQueryState(QueryState queryState) {
        mPropertiesRepository.setQueryState(queryState);
    }

    public LiveData<List<PropertyWithImages>> getPropertyWithImageQuery() {
        return mPropertyWithImageQuery;
    }
}
