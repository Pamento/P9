package com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM;

import androidx.lifecycle.ViewModel;

import com.openclassrooms.realestatemanager.data.reposiotries.PropertiesRepository;

public class DetailViewModel extends ViewModel {

    private final PropertiesRepository mPropertiesRepository;

    public DetailViewModel(PropertiesRepository propertiesRepository) {
        mPropertiesRepository = propertiesRepository;
    }
}
