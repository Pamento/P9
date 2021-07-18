package com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM;

import androidx.lifecycle.ViewModel;

import com.openclassrooms.realestatemanager.data.local.reposiotries.ImageRepository;
import com.openclassrooms.realestatemanager.data.local.reposiotries.PropertiesRepository;

public class ListPropertyViewModel extends ViewModel {

    private final PropertiesRepository mPropertiesRepository;
    private final ImageRepository mImageRepository;

    public ListPropertyViewModel(PropertiesRepository propertiesRepository, ImageRepository imageRepository) {
        mPropertiesRepository = propertiesRepository;
        mImageRepository = imageRepository;
    }
}
