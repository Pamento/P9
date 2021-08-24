package com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM

import android.location.Location
import com.openclassrooms.realestatemanager.data.local.reposiotries.PropertiesRepository
import com.openclassrooms.realestatemanager.data.local.reposiotries.ImageRepository
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.GoogleMap
import androidx.lifecycle.MutableLiveData
import com.openclassrooms.realestatemanager.data.local.entities.PropertyWithImages
import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.openclassrooms.realestatemanager.util.enums.QueryState
import java.util.concurrent.Executor

class MapViewModel(
    private val mPropertiesRepository: PropertiesRepository,
    private val mImageRepository: ImageRepository,
    private val mExecutor: Executor
) : ViewModel() {
    var googleMap: GoogleMap? = null

    // CurrentLocation
    var currentUserLocation: Location? = null
    private val mPropertyWithImageQuery = MutableLiveData<List<PropertyWithImages>>()
    val propertiesWithImagesFromRowQuery: Unit
        get() {
            mExecutor.execute { mPropertyWithImageQuery.postValue(mPropertiesRepository.propertiesWithImagesQuery) }
        }
    val propertyWithImages: LiveData<List<PropertyWithImages>>
        get() = mPropertiesRepository.allPropertiesWithImages
    val simpleSQLiteQuery: LiveData<SimpleSQLiteQuery>
        get() = mPropertiesRepository.rowQueryProperties

    fun setPropertyId(propertyId: String?) {
        mPropertiesRepository.propertY_ID = propertyId
        mImageRepository.propertyId = propertyId
    }

    val queryState: LiveData<QueryState>
        get() = mPropertiesRepository.queryState

    fun setQueryState(queryState: QueryState?) {
        mPropertiesRepository.setQueryState(queryState)
    }

    val propertyWithImageQuery: LiveData<List<PropertyWithImages>>
        get() = mPropertyWithImageQuery
}