package com.openclassrooms.realestatemanager.data.local.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.openclassrooms.realestatemanager.data.local.dao.ImageOfPropertyDao;
import com.openclassrooms.realestatemanager.data.local.dao.SinglePropertyDao;
import com.openclassrooms.realestatemanager.data.local.entities.ImageOfProperty;
import com.openclassrooms.realestatemanager.data.local.entities.SingleProperty;

@Database(entities = {SingleProperty.class, ImageOfProperty.class}, version = 1)
public abstract class RealEstateDatabase extends RoomDatabase {

    private static volatile RealEstateDatabase INSTANCE;

    public abstract SinglePropertyDao singlePropertyDao();
    public abstract ImageOfPropertyDao imageOfPropertyDao();

    public static synchronized RealEstateDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    RealEstateDatabase.class, "real_estate_manager_database")
                    .fallbackToDestructiveMigration().allowMainThreadQueries().build();
        }
        return INSTANCE;
    }

}
