package com.openclassrooms.realestatemanager.injection;

import com.openclassrooms.realestatemanager.data.reposiotries.PropertiesRepository;
import com.openclassrooms.realestatemanager.data.viewModelFactory.ViewModelFactory;

public class Injection {

    public static PropertiesRepository sPropertiesRepository() {
        return PropertiesRepository.getInstance();
    }

    public static ViewModelFactory sViewModelFactory() {
        return new ViewModelFactory(sPropertiesRepository());
    }
}
