package com.openclassrooms.realestatemanager.data.local.reposiotries;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.openclassrooms.realestatemanager.data.local.dao.ImageOfPropertyDao;
import com.openclassrooms.realestatemanager.data.local.entities.ImageOfProperty;

import java.util.List;

public class ImageRepository {

    private static String PROPERTY_ID;
    private final ImageOfPropertyDao mImageOfPropertyDao;
    //private final LiveData<List> imagesOfSingleProperty;

    public ImageRepository(ImageOfPropertyDao imageOfPropertyDao) {
        mImageOfPropertyDao = imageOfPropertyDao;
    }

    // methods
    public void addPropertyImages(List<ImageOfProperty> imagesOfProperties) {
        mImageOfPropertyDao.insertImagesOfProperty(imagesOfProperties);
    }

    public void addPropertyImage(ImageOfProperty imageOfProperty) {
        mImageOfPropertyDao.insertImageOfProperty(imageOfProperty);
    }

    public LiveData<List<ImageOfProperty>> getAllImagesOfProperty(@Nullable String propertyId) {
        if (propertyId == null) return mImageOfPropertyDao.getAllImageForProperty(PROPERTY_ID);
        else return mImageOfPropertyDao.getAllImageForProperty(propertyId);
    }

    // Delete image
    public void deletePropertyImage(int id) {
        mImageOfPropertyDao.deleteImage(id);
    }

    public String getPropertyId() {
        return PROPERTY_ID;
    }

    public void setPropertyId(String propertyId) {
        PROPERTY_ID = propertyId;
    }
}
