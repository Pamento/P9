package com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.sqlite.db.SimpleSQLiteQuery
import com.openclassrooms.realestatemanager.data.local.entities.PropertyWithImages
import com.openclassrooms.realestatemanager.data.local.reposiotries.ImageRepository
import com.openclassrooms.realestatemanager.data.local.reposiotries.PropertiesRepository
import com.openclassrooms.realestatemanager.util.enums.QueryState
import java.util.concurrent.Executor

class ListPropertyViewModel(private val mPropertiesRepository: PropertiesRepository, private val mImageRepository: ImageRepository, private val mExecutor: Executor) : ViewModel() {
    private val mPropertiesWithImagesQuery = MutableLiveData<List<PropertyWithImages>>()
    val simpleSQLiteQuery: LiveData<SimpleSQLiteQuery>
        get() = mPropertiesRepository.rowQueryProperties
    val propertiesWithImagesFromRowQuery: Unit
        get() {
            mExecutor.execute { mPropertiesWithImagesQuery.postValue(mPropertiesRepository.propertiesWithImagesQuery) }
        }
    val propertyWithImages: LiveData<List<PropertyWithImages>>
        get() = mPropertiesRepository.allPropertiesWithImages

    fun setPropertyId(propertyId: String?) {
        mPropertiesRepository.propertY_ID = propertyId
        mImageRepository.propertyId = propertyId
    }

    val queryState: LiveData<QueryState>
        get() = mPropertiesRepository.queryState

    fun setQueryState(queryState: QueryState?) {
        mPropertiesRepository.setQueryState(queryState)
    }

    val propertiesWithImagesFromQuery: LiveData<List<PropertyWithImages>>
        get() = mPropertiesWithImagesQuery
}