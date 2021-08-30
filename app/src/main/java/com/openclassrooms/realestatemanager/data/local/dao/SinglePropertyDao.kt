package com.openclassrooms.realestatemanager.data.local.dao

import android.database.Cursor
import com.openclassrooms.realestatemanager.data.local.entities.SingleProperty
import androidx.lifecycle.LiveData
import androidx.room.*
import com.openclassrooms.realestatemanager.data.local.entities.PropertyWithImages
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface SinglePropertyDao {
    // change pour long
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createSingleProperty(singleProperty: SingleProperty?): Long

    @Update
    fun updateProperty(singleProperty: SingleProperty?): Int

    @Query("SELECT * FROM property WHERE pid = :propertyId")
    fun getSingleProperty(propertyId: String?): LiveData<SingleProperty?>?

    @Query("SELECT * FROM property WHERE pid = :propertyId")
    fun getSinglePropertyProvider(propertyId: String?): Cursor?

    // get two table unified
    @get:Query("SELECT * FROM property")
    @get:Transaction
    val propertyWithImages: LiveData<List<PropertyWithImages?>?>?

    @get:Query("SELECT * FROM property")
    @get:Transaction
    val propertyWithImagesProvider: Cursor?

    @RawQuery
    fun getPropertyWithImageQuery(query: SupportSQLiteQuery?): List<PropertyWithImages?>?
}