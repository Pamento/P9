package com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM;

import androidx.lifecycle.ViewModel;

import com.openclassrooms.realestatemanager.data.reposiotries.PropertiesRepository;

public class ListPropertyViewModel extends ViewModel {

    private final PropertiesRepository mPropertiesRepository;

    public ListPropertyViewModel(PropertiesRepository propertiesRepository) {
        mPropertiesRepository = propertiesRepository;
    }
}
