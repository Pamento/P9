package com.openclassrooms.realestatemanager.data.local.reposiotries;

import androidx.lifecycle.LiveData;

import com.openclassrooms.realestatemanager.data.local.dao.SinglePropertyDao;
import com.openclassrooms.realestatemanager.data.local.entities.PropertyWithImages;
import com.openclassrooms.realestatemanager.data.local.entities.SingleProperty;

import java.util.List;

public class PropertiesRepository {

    //private static volatile PropertiesRepository instance;
    private final SinglePropertyDao mSinglePropertyDao;
    // data
    private final LiveData<List<PropertyWithImages>> mAllProperties;

    public PropertiesRepository(SinglePropertyDao singlePropertyDao) {
        mSinglePropertyDao = singlePropertyDao;
        mAllProperties = mSinglePropertyDao.getPropertyWithImages();
    }

    // Methods
    LiveData<List<PropertyWithImages>> getAllPropertiesWithImages() {
        return mAllProperties;
    }

//    public LiveData<List<SingleProperty>> getAllProperties() {
//        return mSinglePropertyDao.getAllProperties();
//    }

    public LiveData<SingleProperty> getSingleProperty(String propertyId) {
        return mSinglePropertyDao.getSingleProperty(propertyId);
    }

    public void createSingleProperty(SingleProperty singleProperty) {
        mSinglePropertyDao.createSingleProperty(singleProperty);
    }

    public void updateSingleProperty(SingleProperty singleProperty) {
        mSinglePropertyDao.updateProperty(singleProperty);
    }



}
