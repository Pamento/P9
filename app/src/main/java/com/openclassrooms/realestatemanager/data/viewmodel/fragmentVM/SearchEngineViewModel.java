package com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM;

import androidx.lifecycle.ViewModel;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.openclassrooms.realestatemanager.data.local.models.RowQueryEstates;
import com.openclassrooms.realestatemanager.data.local.reposiotries.ImageRepository;
import com.openclassrooms.realestatemanager.data.local.reposiotries.PropertiesRepository;

import java.util.concurrent.Executor;

public class SearchEngineViewModel extends ViewModel {

    private final PropertiesRepository mPropertiesRepository;
    private final ImageRepository mImageRepository;
    private final Executor mExecutor;
    private SupportSQLiteQuery mQuery;

    public SearchEngineViewModel(PropertiesRepository propertiesRepository, ImageRepository imageRepository, Executor executor) {
        mPropertiesRepository = propertiesRepository;
        mImageRepository = imageRepository;
        mExecutor = executor;
    }

    public void buildAndSendSearchEstateQuery(RowQueryEstates rowQueryEstates) {

        // TODO build query(mQuery) and: sendRowEstateQuery(mQuery)
        sendRowEstateQuery(mQuery);
    }

    private void sendRowEstateQuery(SupportSQLiteQuery supportSQLiteQuery) {
        mPropertiesRepository.setRowQueryEstates(mQuery);
    }
}
