package com.openclassrooms.realestatemanager.injection;

import android.content.Context;

import com.openclassrooms.realestatemanager.data.local.database.RealEstateDatabase;
import com.openclassrooms.realestatemanager.data.local.reposiotries.ImageRepository;
import com.openclassrooms.realestatemanager.data.local.reposiotries.PropertiesRepository;
import com.openclassrooms.realestatemanager.data.viewModelFactory.ViewModelFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Injection {

    public static PropertiesRepository sPropertiesRepository(Context context) {
        RealEstateDatabase db = RealEstateDatabase.getInstance(context);
        return new PropertiesRepository(db.singlePropertyDao());
    }

    public static ImageRepository sImageRepository(Context context) {
        RealEstateDatabase db = RealEstateDatabase.getInstance(context);
        return new ImageRepository(db.imageOfPropertyDao());
    }

    public static Executor provideExecutor(){ return Executors.newSingleThreadExecutor(); }

    public static ViewModelFactory sViewModelFactory(Context context) {
        PropertiesRepository propertiesRepository = sPropertiesRepository(context);
        ImageRepository imageRepository = sImageRepository(context);
        Executor executor = provideExecutor();
        return new ViewModelFactory(propertiesRepository, imageRepository, executor);
    }
}
