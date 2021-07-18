package com.openclassrooms.realestatemanager.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.openclassrooms.realestatemanager.data.local.entities.ImageOfProperty;

import java.util.List;

@Dao
public interface ImageOfPropertyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertImagesOfProperty(List<ImageOfProperty> imagesOfProperty);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void insertImageOfProperty(ImageOfProperty imageOfProperty);

    @Query("SELECT * FROM property_image WHERE property_id = :propertyId")
    public LiveData<List<ImageOfProperty>> getAllImageForProperty(String propertyId);

    @Query("DELETE FROM property_image WHERE id = :imageId")
    public void deleteImage(Integer imageId);
}
