package com.openclassrooms.realestatemanager.data.local.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.openclassrooms.realestatemanager.data.local.database.RealEstateDatabase;

public class RealEstatePropertiesProvider extends ContentProvider {

    public static final String AUTHORITY = "com.openclassrooms.realestatemanager.provider";
    public static final Uri URI_DB = Uri.parse("content://" + AUTHORITY + "/*");
    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        if (getContext() != null) {
            final Cursor cursor = RealEstateDatabase.getInstance(getContext()).singlePropertyDao().getPropertyWithImagesProvider();
            cursor.setNotificationUri(getContext().getContentResolver(),uri);
            return cursor;
        }
        throw new IllegalArgumentException("Failed to query row for uri " + uri);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return "vnd.android.cursor.item/" + AUTHORITY + "/*";
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        // Real Estate Manager application don't provide insertion of properties, her provide only read the properties.
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        // Real Estate Manager application don't provide deletion of properties, her provide only read the properties.
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        // Real Estate Manager application don't provide update of properties, her provide only read the properties.
        return 0;
    }
}
