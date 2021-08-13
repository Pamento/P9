package com.openclassrooms.realestatemanager.data.local.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.strictmode.ContentUriWithoutPermissionViolation;

import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import com.openclassrooms.realestatemanager.data.local.database.RealEstateDatabase;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

public class RealEstatePropertiesProviderTest {

    private ContentResolver mContentResolver;
    private static final Integer PROPERTY_ID = 1;

    @Before
    public void setUp() {
        Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().getContext(),
                RealEstateDatabase.class)
                .allowMainThreadQueries().build();
        mContentResolver = InstrumentationRegistry.getInstrumentation().getContext().getContentResolver();
    }

//    @Test
//    public void firstQuery() {
//        final Cursor cursorZero = mContentResolver.query(ContentUris.withAppendedId(RealEstatePropertiesProvider.URI_DB, PROPERTY_ID), null, null, null);
//        assertNotNull(cursorZero);
//        assertThat(cursorZero.getCount(), is(1));
//        cursorZero.close();
//    }

    @Test
    public void insertAndUpdate() {
        // assert empty data base
        final Cursor cursorZero = mContentResolver.query(ContentUris.withAppendedId(RealEstatePropertiesProvider.URI_DB, PROPERTY_ID), null, null, null);
        assertNotNull(cursorZero);
        assertThat(cursorZero.getCount(), is(0));
        // insert
        final Uri propertyUri = mContentResolver.insert(RealEstatePropertiesProvider.URI_DB, generateProperty());
        // get
        final Cursor cursor = mContentResolver.query(ContentUris.withAppendedId(RealEstatePropertiesProvider.URI_DB, PROPERTY_ID),null,null,null,null);
        // assert
        assertThat(propertyUri, notNullValue());
        assertThat(cursor, notNullValue());
        assertThat(cursor.getCount(),is(1));
        assertThat(cursor.moveToFirst(), is(true));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("type")),is("Flat"));
        // update
        final int resUpdate = mContentResolver.update(RealEstatePropertiesProvider.URI_DB, generateUpdateProperty(),null, null);
        // get
        final Cursor cursorUpdate = mContentResolver.query(ContentUris.withAppendedId(RealEstatePropertiesProvider.URI_DB, PROPERTY_ID),null,null,null,null);
        // assert
        assertThat(resUpdate, notNullValue());
        assertThat(cursorUpdate, notNullValue());
        assertThat(cursorUpdate.getCount(), is(1));
        assertThat(cursor.moveToFirst(), is(true));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("type")),is("Flat"));
        cursor.close();
    }

    private ContentValues generateProperty() {
        final ContentValues cv = new ContentValues();
        cv.put("id", "123456789");
        cv.put("type", "Flat");
        cv.put("description", "description");
        cv.put("surface", 120);
        cv.put("price", 30100300);
        cv.put("rooms", 7);
        cv.put("bedroom", 2);
        cv.put("bathroom", 2);
        cv.put("dateRegister", "1628879252929");
        cv.put("dateSold", 0);
        cv.put("address1", "127 Prince St");
        cv.put("address2", "Apt 6/7a");
        cv.put("city", "New York");
        cv.put("quarter", "Manhattan");
        cv.put("postalCode", "10012");
        cv.put("location", "");
        cv.put("amenities", "bus-playground");
        cv.put("agent", "Morgan Freeman");
        return cv;
    }

    private ContentValues generateUpdateProperty() {
        final ContentValues cv = new ContentValues();
        cv.put("id", "123456789");
        cv.put("type", "Apartment");
        cv.put("description", "description");
        cv.put("surface", 120);
        cv.put("price", 30100300);
        cv.put("rooms", 7);
        cv.put("bedroom", 2);
        cv.put("bathroom", 2);
        cv.put("dateRegister", "1628879252929");
        cv.put("dateSold", 0);
        cv.put("address1", "127 Prince St");
        cv.put("address2", "Apt 6/7a");
        cv.put("city", "New York");
        cv.put("quarter", "Manhattan");
        cv.put("postalCode", "10012");
        cv.put("location", "");
        cv.put("amenities", "bus-playground");
        cv.put("agent", "Morgan Freeman");
        return cv;
    }
}