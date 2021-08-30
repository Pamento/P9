package com.openclassrooms.realestatemanager.data.local.dao

import com.openclassrooms.realestatemanager.data.local.entities.ImageOfProperty
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ImageOfPropertyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertImagesOfProperty(imagesOfProperty: List<ImageOfProperty?>?): LongArray?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertImageOfProperty(imageOfProperty: ImageOfProperty?): Long

    @Update
    fun updateImageOfProperty(imageOfProperty: ImageOfProperty?): Int

    @Query("SELECT * FROM property_image WHERE property_id = :propertyId")
    fun getAllImageForProperty(propertyId: String?): LiveData<List<ImageOfProperty?>?>?

    @Query("DELETE FROM property_image WHERE id = :imageId")
    fun deleteImage(imageId: Int?)
}