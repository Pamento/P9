package com.openclassrooms.realestatemanager.data.reposiotries;

public class PropertiesRepository {

    private static volatile PropertiesRepository instance;

    public PropertiesRepository() {
    }

    public static PropertiesRepository getInstance() {
        if (instance == null) instance = new PropertiesRepository();
        return instance;
    }
}
