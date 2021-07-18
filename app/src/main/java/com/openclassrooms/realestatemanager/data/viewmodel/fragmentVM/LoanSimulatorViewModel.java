package com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM;

import androidx.lifecycle.ViewModel;

import com.openclassrooms.realestatemanager.data.local.reposiotries.PropertiesRepository;

public class LoanSimulatorViewModel extends ViewModel {

    private final PropertiesRepository mPropertiesRepository;

    public LoanSimulatorViewModel(PropertiesRepository propertiesRepository) {
        mPropertiesRepository = propertiesRepository;
    }
}
