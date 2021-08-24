package com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM

import com.openclassrooms.realestatemanager.data.local.reposiotries.PropertiesRepository
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.data.local.models.RowQueryEstates

class SearchEngineViewModel(private val mPropertiesRepository: PropertiesRepository) : ViewModel() {
    fun buildAndSendSearchEstateQuery(rowQueryEstates: RowQueryEstates?) {
        mPropertiesRepository.buildAndSetSearchEstateQuery(rowQueryEstates)
    }
}