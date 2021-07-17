package com.openclassrooms.realestatemanager.data.viewmodel;

import androidx.lifecycle.ViewModel;

import com.openclassrooms.realestatemanager.data.reposiotries.PropertiesRepository;

public class MainActivityViewModel extends ViewModel {

    private final PropertiesRepository mPropertiesRepository;

    public MainActivityViewModel(PropertiesRepository repository) {
        mPropertiesRepository = repository;
    }
}
