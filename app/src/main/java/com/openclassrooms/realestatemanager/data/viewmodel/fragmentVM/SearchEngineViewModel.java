package com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM;

import androidx.lifecycle.ViewModel;

import com.openclassrooms.realestatemanager.data.reposiotries.PropertiesRepository;

public class SearchEngineViewModel extends ViewModel {

    private final PropertiesRepository mPropertiesRepository;

    public SearchEngineViewModel(PropertiesRepository propertiesRepository) {
        mPropertiesRepository = propertiesRepository;
    }
}
