package com.openclassrooms.realestatemanager.data.viewModelFactory

import com.openclassrooms.realestatemanager.data.local.reposiotries.PropertiesRepository
import com.openclassrooms.realestatemanager.data.local.reposiotries.ImageRepository
import com.openclassrooms.realestatemanager.data.remote.repository.GoogleMapsRepository
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.AddPropertyViewModel
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.DetailViewModel
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.EditPropertyViewModel
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.ListPropertyViewModel
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.LoanSimulatorViewModel
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.MapViewModel
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.SearchEngineViewModel
import java.lang.IllegalArgumentException
import java.util.concurrent.Executor

class ViewModelFactory(
    private val mPropertiesRepository: PropertiesRepository,
    private val mImageRepository: ImageRepository,
    private val mGoogleMapsRepository: GoogleMapsRepository,
    private val mExecutor: Executor
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AddPropertyViewModel::class.java) -> AddPropertyViewModel(
                mPropertiesRepository,
                mImageRepository,
                mGoogleMapsRepository,
                mExecutor
            ) as T
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> DetailViewModel(
                mPropertiesRepository,
                mImageRepository,
                mGoogleMapsRepository
            ) as T
            modelClass.isAssignableFrom(EditPropertyViewModel::class.java) -> EditPropertyViewModel(
                mPropertiesRepository,
                mImageRepository,
                mGoogleMapsRepository,
                mExecutor
            ) as T
            modelClass.isAssignableFrom(ListPropertyViewModel::class.java) -> ListPropertyViewModel(
                mPropertiesRepository,
                mImageRepository,
                mExecutor
            ) as T
            modelClass.isAssignableFrom(LoanSimulatorViewModel::class.java) -> LoanSimulatorViewModel(
                mPropertiesRepository
            ) as T
            modelClass.isAssignableFrom(MapViewModel::class.java) -> MapViewModel(
                mPropertiesRepository,
                mImageRepository,
                mExecutor
            ) as T
            modelClass.isAssignableFrom(SearchEngineViewModel::class.java) -> SearchEngineViewModel(
                mPropertiesRepository
            ) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}