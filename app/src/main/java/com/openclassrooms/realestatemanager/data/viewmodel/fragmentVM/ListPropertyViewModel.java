package com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.openclassrooms.realestatemanager.data.local.entities.PropertyWithImages;
import com.openclassrooms.realestatemanager.data.local.reposiotries.ImageRepository;
import com.openclassrooms.realestatemanager.data.local.reposiotries.PropertiesRepository;
import com.openclassrooms.realestatemanager.util.enums.QueryState;

import java.util.List;
import java.util.concurrent.Executor;

public class ListPropertyViewModel extends ViewModel {

    private final PropertiesRepository mPropertiesRepository;
    private final ImageRepository mImageRepository;
    private final Executor mExecutor;
    private final MutableLiveData<List<PropertyWithImages>> mPropertiesWithImagesQuery = new MutableLiveData<>();

    public ListPropertyViewModel(PropertiesRepository propertiesRepository, ImageRepository imageRepository, Executor executor) {
        mPropertiesRepository = propertiesRepository;
        mImageRepository = imageRepository;
        mExecutor = executor;
    }

    public LiveData<SimpleSQLiteQuery> getSimpleSQLiteQuery() {
        return mPropertiesRepository.getRowQueryProperties();
    }

    public void getPropertiesWithImagesFromRowQuery() {
        mExecutor.execute(() ->
                mPropertiesWithImagesQuery.postValue(mPropertiesRepository.getPropertiesWithImagesQuery())
        );
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

    public LiveData<List<PropertyWithImages>> getPropertiesWithImagesFromQuery() {
        return mPropertiesWithImagesQuery;
    }
}
