package com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.openclassrooms.realestatemanager.data.local.entities.ImageOfProperty;
import com.openclassrooms.realestatemanager.data.local.entities.SingleProperty;
import com.openclassrooms.realestatemanager.data.local.reposiotries.ImageRepository;
import com.openclassrooms.realestatemanager.data.local.reposiotries.PropertiesRepository;

import java.util.List;

public class DetailViewModel extends ViewModel {

    private final PropertiesRepository mPropertiesRepository;
    private final ImageRepository mImageRepository;

    public DetailViewModel(PropertiesRepository propertiesRepository, ImageRepository imageRepository) {
        mPropertiesRepository = propertiesRepository;
        mImageRepository = imageRepository;
    }

    public LiveData<SingleProperty> getSingleProperty() {
        return mPropertiesRepository.getSingleProperty(null);
    }
    public LiveData<List<ImageOfProperty>> getImagesOfProperty() {
        return mImageRepository.getAllImagesOfProperty(null);
    }
}
