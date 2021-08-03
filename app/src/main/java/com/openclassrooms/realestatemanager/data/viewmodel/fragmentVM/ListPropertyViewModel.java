package com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.openclassrooms.realestatemanager.data.local.entities.ImageOfProperty;
import com.openclassrooms.realestatemanager.data.local.entities.PropertyWithImages;
import com.openclassrooms.realestatemanager.data.local.reposiotries.ImageRepository;
import com.openclassrooms.realestatemanager.data.local.reposiotries.PropertiesRepository;

import java.util.List;

public class ListPropertyViewModel extends ViewModel {

    private final PropertiesRepository mPropertiesRepository;
    private final ImageRepository mImageRepository;

    public ListPropertyViewModel(PropertiesRepository propertiesRepository, ImageRepository imageRepository) {
        mPropertiesRepository = propertiesRepository;
        mImageRepository = imageRepository;
    }

    public SupportSQLiteQuery getSimpleSQLiteQuery() {
        Log.i("AddProperty", "LIST_VM__ getSimpleSQLiteQuery: SQLite__SQLite__SQLite__SQLite__SQLite__");
        Log.i("AddProperty", "LIST_VM__ getSimpleSQLiteQuery: SQLite__SQLite__SQLite__SQLite__SQLite__");
        return mPropertiesRepository.getRowQueryEstates();
    }

    public List<PropertyWithImages> getPropertiesWithImagesFromRowQuery() {
        return mPropertiesRepository.getPropertiesWithImagesQuery();
    }

    public LiveData<List<PropertyWithImages>> getPropertyWithImages() {
        return mPropertiesRepository.getAllPropertiesWithImages();
    }

    public LiveData<List<ImageOfProperty>> getImagesOfProperty(String propertyId) {
        return mImageRepository.getAllImagesOfProperty(propertyId);
    }

    public void setPropertyId(String propertyId) {
        mPropertiesRepository.setPROPERTY_ID(propertyId);
        mImageRepository.setPropertyId(propertyId);
    }
}
