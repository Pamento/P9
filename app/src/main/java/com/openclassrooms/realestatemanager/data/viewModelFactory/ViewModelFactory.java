package com.openclassrooms.realestatemanager.data.viewModelFactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.openclassrooms.realestatemanager.data.local.reposiotries.ImageRepository;
import com.openclassrooms.realestatemanager.data.local.reposiotries.PropertiesRepository;
import com.openclassrooms.realestatemanager.data.remote.repository.GoogleMapsRepository;
import com.openclassrooms.realestatemanager.data.viewmodel.MainActivityViewModel;
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.AddPropertyViewModel;
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.DetailViewModel;
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.EditPropertyViewModel;
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.ListPropertyViewModel;
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.LoanSimulatorViewModel;
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.MapViewModel;
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.SearchEngineViewModel;

import java.util.concurrent.Executor;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final PropertiesRepository mPropertiesRepository;
    private final ImageRepository mImageRepository;
    private final GoogleMapsRepository mGoogleMapsRepository;
    private final Executor mExecutor;

    public ViewModelFactory(PropertiesRepository propertiesRepository,
                            ImageRepository imageRepository,
                            GoogleMapsRepository googleMapsRepository,
                            Executor executor) {
        mPropertiesRepository = propertiesRepository;
        mImageRepository = imageRepository;
        mGoogleMapsRepository = googleMapsRepository;
        mExecutor = executor;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainActivityViewModel.class)) {
            return (T) new MainActivityViewModel(mPropertiesRepository, mImageRepository, mExecutor);
        }
        if (modelClass.isAssignableFrom(AddPropertyViewModel.class)) {
            return (T) new AddPropertyViewModel(mPropertiesRepository, mImageRepository, mGoogleMapsRepository, mExecutor);
        }
        if (modelClass.isAssignableFrom(DetailViewModel.class)) {
            return (T) new DetailViewModel(mPropertiesRepository, mImageRepository);
        }
        if (modelClass.isAssignableFrom(EditPropertyViewModel.class)) {
            return (T) new EditPropertyViewModel(mPropertiesRepository, mImageRepository, mGoogleMapsRepository, mExecutor);
        }
        if (modelClass.isAssignableFrom(ListPropertyViewModel.class)) {
            return (T) new ListPropertyViewModel(mPropertiesRepository, mImageRepository);
        }
        if (modelClass.isAssignableFrom(LoanSimulatorViewModel.class)) {
            return (T) new LoanSimulatorViewModel(mPropertiesRepository);
        }
        if (modelClass.isAssignableFrom(MapViewModel.class)) {
            return (T) new MapViewModel(mPropertiesRepository, mImageRepository);
        }
        if (modelClass.isAssignableFrom(SearchEngineViewModel.class)) {
            return (T) new SearchEngineViewModel(mPropertiesRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
