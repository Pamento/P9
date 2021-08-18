package com.openclassrooms.realestatemanager.data.local.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.openclassrooms.realestatemanager.data.local.database.RealEstateDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
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

    @After
    public void tearDown() throws Exception {
        InstrumentationRegistry.getInstrumentation().getTargetContext().deleteDatabase("real_estate_manager_database");
    }

    @Test
    public void insertAndUpdate_test() {
        // assert empty data base
        final Cursor cursor = mContentResolver.query(ContentUris.withAppendedId(RealEstatePropertiesProvider.URI_DB, PROPERTY_ID), null, null, null);
        assertNotNull(cursor);
        assertThat(cursor.getCount(), is(0));
        cursor.close();
        // insert
        final Uri propertyUri = mContentResolver.insert(RealEstatePropertiesProvider.URI_DB, generateProperty());
        // get
        final Cursor cursor2 = mContentResolver.query(ContentUris.withAppendedId(RealEstatePropertiesProvider.URI_DB, PROPERTY_ID),null,null,null,null);
        // assert
        assertThat(propertyUri, notNullValue());
        assertThat(cursor2, notNullValue());
        assertThat(cursor2.getCount(),is(1));
        assertThat(cursor2.moveToFirst(), is(true));
        assertThat(cursor2.getString(cursor2.getColumnIndexOrThrow("type")),is("Flat"));
        cursor2.close();

        // update
        final int resUpdate = mContentResolver.update(RealEstatePropertiesProvider.URI_DB, generateUpdateProperty(),null, null);
        // get
        final Cursor cursorUpdate = mContentResolver.query(ContentUris.withAppendedId(RealEstatePropertiesProvider.URI_DB, PROPERTY_ID),null,null,null,null);
        // assert
        assertThat(resUpdate, notNullValue());
        assertThat(cursorUpdate, notNullValue());
        assertThat(cursorUpdate.getCount(), is(1));
        assertThat(cursorUpdate.moveToFirst(), is(true));
        assertThat(cursorUpdate.getString(cursorUpdate.getColumnIndexOrThrow("type")),is("Apartment"));
        cursorUpdate.close();
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