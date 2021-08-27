package com.openclassrooms.realestatemanager.injection;

import android.content.Context;

import com.openclassrooms.realestatemanager.data.local.database.RealEstateDatabase;
import com.openclassrooms.realestatemanager.data.local.reposiotries.ImageRepository;
import com.openclassrooms.realestatemanager.data.local.reposiotries.PropertiesRepository;
import com.openclassrooms.realestatemanager.data.remote.repository.GoogleMapsRepository;
import com.openclassrooms.realestatemanager.data.viewModelFactory.ViewModelFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Injection {

    public static PropertiesRepository sPropertiesRepository(Context context) {
        RealEstateDatabase db = RealEstateDatabase.getInstance(context);
        return PropertiesRepository.getInstance(db.singlePropertyDao());
    }

    public static ImageRepository sImageRepository(Context context) {
        RealEstateDatabase db = RealEstateDatabase.getInstance(context);
        return ImageRepository.getInstance(db.imageOfPropertyDao());
    }

    public static GoogleMapsRepository sGoogleMapsRepository() {
        return GoogleMapsRepository.getInstance();
    }

    public static Executor provideExecutor(){ return Executors.newSingleThreadExecutor(); }

    public static ViewModelFactory sViewModelFactory(Context context) {
        PropertiesRepository propertiesRepository = sPropertiesRepository(context);
        ImageRepository imageRepository = sImageRepository(context);
        GoogleMapsRepository googleMapsRepository = sGoogleMapsRepository();
        Executor executor = provideExecutor();
        return new ViewModelFactory(propertiesRepository, imageRepository, googleMapsRepository, executor);
    }
}
