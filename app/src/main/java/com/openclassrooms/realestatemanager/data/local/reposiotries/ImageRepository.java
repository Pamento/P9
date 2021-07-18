package com.openclassrooms.realestatemanager.data.local.reposiotries;

import androidx.lifecycle.LiveData;

import com.openclassrooms.realestatemanager.data.local.dao.ImageOfPropertyDao;
import com.openclassrooms.realestatemanager.data.local.entities.ImageOfProperty;

import java.util.List;

public class ImageRepository {

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

    public LiveData<List<ImageOfProperty>> getAllImagesOfProperty(String propertyId) {
        return mImageOfPropertyDao.getAllImageForProperty(propertyId);
    }

    // Delete image
    public void deletePropertyImage(int id) {
        mImageOfPropertyDao.deleteImage(id);
    }

}
