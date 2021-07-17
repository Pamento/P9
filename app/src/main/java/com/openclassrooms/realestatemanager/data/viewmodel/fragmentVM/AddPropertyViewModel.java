package com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM;

import androidx.lifecycle.ViewModel;

import com.openclassrooms.realestatemanager.data.reposiotries.PropertiesRepository;

public class AddPropertyViewModel extends ViewModel {

    private final PropertiesRepository mPropertiesRepository;

    public AddPropertyViewModel(PropertiesRepository propertiesRepository) {
        mPropertiesRepository = propertiesRepository;
    }
}
