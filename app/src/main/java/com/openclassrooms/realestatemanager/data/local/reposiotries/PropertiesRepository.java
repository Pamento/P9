package com.openclassrooms.realestatemanager.data.local.reposiotries;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.openclassrooms.realestatemanager.data.local.dao.SinglePropertyDao;
import com.openclassrooms.realestatemanager.data.local.entities.PropertyWithImages;
import com.openclassrooms.realestatemanager.data.local.entities.SingleProperty;
import com.openclassrooms.realestatemanager.util.enums.QueryState;

import java.util.List;

public class PropertiesRepository {

    private static volatile PropertiesRepository instance;
    private final SinglePropertyDao mSinglePropertyDao;
    // data
    private final LiveData<List<PropertyWithImages>> mAllProperties;
    private String PROPERTY_ID;
    private final MutableLiveData<SimpleSQLiteQuery> mRowQueryEstates;
    private final MutableLiveData<QueryState> mQueryState = new MutableLiveData<>();

    public PropertiesRepository(SinglePropertyDao singlePropertyDao) {
        mSinglePropertyDao = singlePropertyDao;
        mAllProperties = mSinglePropertyDao.getPropertyWithImages();
        mRowQueryEstates = new MutableLiveData<>();
        mQueryState.setValue(QueryState.NULL);
    }

    public static synchronized PropertiesRepository getInstance(SinglePropertyDao singlePropertyDao) {
        if (instance == null) {
            instance = new PropertiesRepository(singlePropertyDao);
        }
        return instance;
    }

    // Methods
    public LiveData<List<PropertyWithImages>> getAllPropertiesWithImages() {
        return mAllProperties;
    }

    public List<PropertyWithImages> getPropertiesWithImagesQuery() {
        return mSinglePropertyDao.getPropertyWithImageQuery(mRowQueryEstates.getValue());
    }

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

    public void setPROPERTY_ID(String PROPERTY_ID) {
        this.PROPERTY_ID = PROPERTY_ID;
    }

    public String getPROPERTY_ID() {
        return PROPERTY_ID;
    }

    public LiveData<QueryState> getQueryState() {
        return mQueryState;
    }

    public void setQueryState(QueryState queryState) {
        mQueryState.setValue(queryState);
    }

    public LiveData<SimpleSQLiteQuery> getRowQueryProperties() {
        return mRowQueryEstates;
    }

    public void setRowQueryEstates(SimpleSQLiteQuery rowQueryEstates) {
        mQueryState.setValue(QueryState.QUERY);
        mRowQueryEstates.setValue(rowQueryEstates);
    }
}
