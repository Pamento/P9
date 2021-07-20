package com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM;

import androidx.lifecycle.ViewModel;

import com.openclassrooms.realestatemanager.data.local.entities.ImageOfProperty;
import com.openclassrooms.realestatemanager.data.local.entities.SingleProperty;
import com.openclassrooms.realestatemanager.data.local.reposiotries.ImageRepository;
import com.openclassrooms.realestatemanager.data.local.reposiotries.PropertiesRepository;

import java.util.concurrent.Executor;

public class AddPropertyViewModel extends ViewModel {

    private final PropertiesRepository mPropertiesRepository;
    private final ImageRepository mImageRepository;
    private final Executor mExecutor;
    private SingleProperty mSingleProperty;
    private ImageOfProperty mImageOfProperty;

    public AddPropertyViewModel(PropertiesRepository propertiesRepository, ImageRepository imageRepository, Executor executor) {
        mPropertiesRepository = propertiesRepository;
        mImageRepository = imageRepository;
        mExecutor = executor;
    }

    public SingleProperty getSingleProperty() {
        return mSingleProperty;
    }

    public void setSingleProperty(SingleProperty singleProperty) {
        mSingleProperty = singleProperty;
    }

    public ImageOfProperty getImageOfProperty() {
        return mImageOfProperty;
    }

    public void setImageOfProperty(ImageOfProperty imageOfProperty) {
        mImageOfProperty = imageOfProperty;
    }

    // Handle data
    public boolean createSingleProperty() {
        mExecutor.execute(() -> {
            mPropertiesRepository.createSingleProperty(mSingleProperty);
        });
        return true;
    }
}
