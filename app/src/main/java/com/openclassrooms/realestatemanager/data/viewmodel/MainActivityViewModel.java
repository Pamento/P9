package com.openclassrooms.realestatemanager.data.viewmodel;

import androidx.lifecycle.ViewModel;

import com.openclassrooms.realestatemanager.data.local.reposiotries.ImageRepository;
import com.openclassrooms.realestatemanager.data.local.reposiotries.PropertiesRepository;

import java.util.concurrent.Executor;

public class MainActivityViewModel extends ViewModel {

    private final PropertiesRepository mPropertiesRepository;
    private final ImageRepository mImageRepository;
    private final Executor mExecutor;

    public MainActivityViewModel(PropertiesRepository repository, ImageRepository imageRepository, Executor executor) {
        mPropertiesRepository = repository;
        mImageRepository = imageRepository;
        mExecutor = executor;
    }
}
