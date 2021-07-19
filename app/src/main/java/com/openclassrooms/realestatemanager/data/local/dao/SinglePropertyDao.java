package com.openclassrooms.realestatemanager.data.local.dao;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.openclassrooms.realestatemanager.data.local.entities.PropertyWithImages;
import com.openclassrooms.realestatemanager.data.local.entities.SingleProperty;

import java.util.List;

@Dao
public interface SinglePropertyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void createSingleProperty(SingleProperty singleProperty);

    @Update
    public void updateProperty(SingleProperty singleProperty);

    @Query("SELECT * FROM property WHERE pid = :propertyId")
    public LiveData<SingleProperty> getSingleProperty(String propertyId);

    @Query("SELECT * FROM property WHERE pid = :propertyId")
    Cursor getSinglePropertyProvider(String propertyId);

    // What that return ?
//    @Query("SELECT pid,type,price FROM property")
//    public LiveData<List<SingleProperty>> getAllProperties();

    // get two table unified
    @Transaction
    @Query("SELECT * FROM property")
    public LiveData<List<PropertyWithImages>> getPropertyWithImages();

    @Transaction
    @Query("SELECT * FROM property")
    Cursor getPropertyWithImagesProvider();
}
