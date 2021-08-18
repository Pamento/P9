package com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.openclassrooms.realestatemanager.data.local.entities.PropertyWithImages;
import com.openclassrooms.realestatemanager.data.local.reposiotries.ImageRepository;
import com.openclassrooms.realestatemanager.data.local.reposiotries.PropertiesRepository;
import com.openclassrooms.realestatemanager.util.enums.QueryState;

import java.util.List;

public class ListPropertyViewModel extends ViewModel {

    private final PropertiesRepository mPropertiesRepository;
    private final ImageRepository mImageRepository;

    public ListPropertyViewModel(PropertiesRepository propertiesRepository, ImageRepository imageRepository) {
        mPropertiesRepository = propertiesRepository;
        mImageRepository = imageRepository;
    }

    public LiveData<SimpleSQLiteQuery> getSimpleSQLiteQuery() {
        Log.i("AddProperty", "LIST_VM__ getSimpleSQLiteQuery: SQLite__SQLite__SQLite__SQLite__SQLite__");
        Log.i("AddProperty", "LIST_VM__ getSimpleSQLiteQuery: SQLite__SQLite__SQLite__SQLite__SQLite__");
        return mPropertiesRepository.getRowQueryProperties();
    }

    public List<PropertyWithImages> getPropertiesWithImagesFromRowQuery() {
        return mPropertiesRepository.getPropertiesWithImagesQuery();
    }

    public LiveData<List<PropertyWithImages>> getPropertyWithImages() {
        return mPropertiesRepository.getAllPropertiesWithImages();
    }

    public void setPropertyId(String propertyId) {
        mPropertiesRepository.setPROPERTY_ID(propertyId);
        mImageRepository.setPropertyId(propertyId);
    }

    public LiveData<QueryState> getQueryState() {
        return mPropertiesRepository.getQueryState();
    }

    public void setQueryState(QueryState queryState) {
        mPropertiesRepository.setQueryState(queryState);
    }
}
