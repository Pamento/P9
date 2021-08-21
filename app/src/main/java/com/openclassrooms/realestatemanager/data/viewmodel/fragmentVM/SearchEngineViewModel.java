package com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM;

import androidx.lifecycle.ViewModel;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.openclassrooms.realestatemanager.data.local.models.RowQueryEstates;
import com.openclassrooms.realestatemanager.data.local.reposiotries.PropertiesRepository;

import java.util.ArrayList;
import java.util.List;

import static com.openclassrooms.realestatemanager.util.Constants.ColumnName.*;

public class SearchEngineViewModel extends ViewModel {

    private final PropertiesRepository mPropertiesRepository;

    public SearchEngineViewModel(PropertiesRepository propertiesRepository) {
        mPropertiesRepository = propertiesRepository;
    }

    public void buildAndSendSearchEstateQuery(RowQueryEstates rowQueryEstates) {
        mPropertiesRepository.buildAndSetSearchEstateQuery(rowQueryEstates);
    }
}
