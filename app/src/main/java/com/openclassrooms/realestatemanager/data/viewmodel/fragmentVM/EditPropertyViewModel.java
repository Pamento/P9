package com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM;

import androidx.lifecycle.ViewModel;

import com.openclassrooms.realestatemanager.data.reposiotries.PropertiesRepository;

public class EditPropertyViewModel extends ViewModel {

    private final PropertiesRepository mPropertiesRepository;

    public EditPropertyViewModel(PropertiesRepository propertiesRepository) {
        mPropertiesRepository = propertiesRepository;
    }
}
