package com.openclassrooms.realestatemanager.data.local.reposiotries;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.openclassrooms.realestatemanager.data.local.dao.SinglePropertyDao;
import com.openclassrooms.realestatemanager.data.local.entities.PropertyWithImages;
import com.openclassrooms.realestatemanager.data.local.entities.SingleProperty;
import com.openclassrooms.realestatemanager.data.local.models.RowQueryEstates;

import java.util.List;

public class PropertiesRepository {
    private static final String TAG = "AddProperty";
    //private static volatile PropertiesRepository instance;
    private final SinglePropertyDao mSinglePropertyDao;
    // data
    private final LiveData<List<PropertyWithImages>> mAllProperties;
    private String PROPERTY_ID;
    private SupportSQLiteQuery mRowQueryEstates;

    public PropertiesRepository(SinglePropertyDao singlePropertyDao) {
        mSinglePropertyDao = singlePropertyDao;
        mAllProperties = mSinglePropertyDao.getPropertyWithImages();
    }

    // Methods
    public LiveData<List<PropertyWithImages>> getAllPropertiesWithImages() {
        return mAllProperties;
    }

    public LiveData<List<PropertyWithImages>> getPropertiesWithImagesQuery() {
        return mSinglePropertyDao.getPropertyWithImageQuery(mRowQueryEstates);
    }

//    public LiveData<List<SingleProperty>> getAllProperties() {
//        return mSinglePropertyDao.getAllProperties();
//    }

    public LiveData<SingleProperty> getSingleProperty(@Nullable String propertyId) {
        // If param: propertyId is null, the cal came for DetailFragment
        // On click on the item of List of Properties, the PROPERTY_ID is set
        if (propertyId != null) return mSinglePropertyDao.getSingleProperty(propertyId);
        else return mSinglePropertyDao.getSingleProperty(PROPERTY_ID);
    }

    public long createSingleProperty(SingleProperty singleProperty) {
        return mSinglePropertyDao.createSingleProperty(singleProperty);
    }

    public int updateSingleProperty(SingleProperty singleProperty) {
        return mSinglePropertyDao.updateProperty(singleProperty);
    }

//    public String getPROPERTY_ID() {
//        return PROPERTY_ID;
//    }

    public void setPROPERTY_ID(String PROPERTY_ID) {
        Log.i(TAG, "PROP_REPO__ setPROPERTY_ID: " + PROPERTY_ID);
        this.PROPERTY_ID = PROPERTY_ID;
    }

    public SupportSQLiteQuery getRowQueryEstates() {
        return mRowQueryEstates;
    }

    public void setRowQueryEstates(SupportSQLiteQuery rowQueryEstates) {
        mRowQueryEstates = rowQueryEstates;
    }
}
