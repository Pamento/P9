package com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM;

import androidx.lifecycle.ViewModel;

import com.openclassrooms.realestatemanager.data.local.entities.ImageOfProperty;
import com.openclassrooms.realestatemanager.data.local.entities.SingleProperty;
import com.openclassrooms.realestatemanager.data.local.reposiotries.ImageRepository;
import com.openclassrooms.realestatemanager.data.local.reposiotries.PropertiesRepository;

import java.util.UUID;
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
        mExecutor.execute(() -> mPropertiesRepository.createSingleProperty(mSingleProperty));
        return true;
    }

    public void createNewProperty(String type,
                                  String description,
                                  Integer surface,
                                  Integer price,
                                  Integer rooms,
                                  Integer bedroom,
                                  Integer bathroom,
                                  Integer dateInit,
                                  Integer dateSold,
                                  String address1,
                                  String address2,
                                  String city,
                                  String quarter,
                                  Integer postalCode,
                                  String amenities,
                                  String agent) {

        mSingleProperty = new SingleProperty();
        mSingleProperty.id = UUID.randomUUID().toString();
        mSingleProperty.type = type.equals("") ? null : type;
        mSingleProperty.description = description.equals("") ? null : description;
        mSingleProperty.surface = surface;
        mSingleProperty.price = price;
        mSingleProperty.rooms = rooms;
        mSingleProperty.bedroom = bedroom;
        mSingleProperty.bathroom = bathroom;
        mSingleProperty.dateRegister = dateInit;
        mSingleProperty.dateSold = dateSold;
        mSingleProperty.address1 = address1;
        mSingleProperty.address2 = address2;
        mSingleProperty.quarter = quarter.equals("") ? null : quarter;
        mSingleProperty.city = city.equals("") ? null : city;
        mSingleProperty.postalCode = postalCode;
        mSingleProperty.amenities = amenities.equals("") ? null : amenities;
        mSingleProperty.agent = agent;
    }
}
