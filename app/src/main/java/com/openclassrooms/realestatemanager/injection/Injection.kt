package com.openclassrooms.realestatemanager.injection

import android.content.Context
import com.openclassrooms.realestatemanager.data.local.reposiotries.PropertiesRepository
import com.openclassrooms.realestatemanager.data.local.database.RealEstateDatabase
import com.openclassrooms.realestatemanager.data.local.reposiotries.ImageRepository
import com.openclassrooms.realestatemanager.data.remote.repository.GoogleMapsRepository
import com.openclassrooms.realestatemanager.data.viewModelFactory.ViewModelFactory
import java.util.concurrent.Executor
import java.util.concurrent.Executors

object Injection {

    private fun sPropertiesRepository(context: Context): PropertiesRepository {
        val db = context.let { RealEstateDatabase.getInstance(it) }
        return PropertiesRepository.getInstance(db?.singlePropertyDao())
    }

    private fun sImageRepository(context: Context): ImageRepository {
        val db = context.let { RealEstateDatabase.getInstance(it) }
        return ImageRepository.getInstance(db?.imageOfPropertyDao())
    }

    private fun sGoogleMapsRepository(): GoogleMapsRepository {
        return GoogleMapsRepository.getInstance()
    }

    private fun provideExecutor(): Executor {
        return Executors.newSingleThreadExecutor()
    }

    @JvmStatic
    fun sViewModelFactory(context: Context): ViewModelFactory {
        val propertiesRepository = sPropertiesRepository(context)
        val imageRepository = sImageRepository(context)
        val googleMapsRepository = sGoogleMapsRepository()
        val executor = provideExecutor()
        return ViewModelFactory(
            propertiesRepository,
            imageRepository,
            googleMapsRepository,
            executor
        )
    }
}