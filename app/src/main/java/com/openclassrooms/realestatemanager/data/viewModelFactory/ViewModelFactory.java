package com.openclassrooms.realestatemanager.data.viewModelFactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.openclassrooms.realestatemanager.data.reposiotries.PropertiesRepository;
import com.openclassrooms.realestatemanager.data.viewmodel.MainActivityViewModel;
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.AddPropertyViewModel;
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.DetailViewModel;
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.EditPropertyViewModel;
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.ListPropertyViewModel;
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.LoanSimulatorViewModel;
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.MapViewModel;
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.SearchEngineViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    PropertiesRepository mPropertiesRepository;

    public ViewModelFactory(PropertiesRepository propertiesRepository) {
        mPropertiesRepository = propertiesRepository;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainActivityViewModel.class)) {
            return (T) new MainActivityViewModel(mPropertiesRepository);
        }
        if (modelClass.isAssignableFrom(AddPropertyViewModel.class)) {
            return (T) new AddPropertyViewModel(mPropertiesRepository);
        }
        if (modelClass.isAssignableFrom(DetailViewModel.class)) {
            return (T) new DetailViewModel(mPropertiesRepository);
        }
        if (modelClass.isAssignableFrom(EditPropertyViewModel.class)) {
            return (T) new EditPropertyViewModel(mPropertiesRepository);
        }
        if (modelClass.isAssignableFrom(ListPropertyViewModel.class)) {
            return (T) new ListPropertyViewModel(mPropertiesRepository);
        }
        if (modelClass.isAssignableFrom(LoanSimulatorViewModel.class)) {
            return (T) new LoanSimulatorViewModel(mPropertiesRepository);
        }
        if (modelClass.isAssignableFrom(MapViewModel.class)) {
            return (T) new MapViewModel(mPropertiesRepository);
        }
        if (modelClass.isAssignableFrom(SearchEngineViewModel.class)) {
            return (T) new SearchEngineViewModel(mPropertiesRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
