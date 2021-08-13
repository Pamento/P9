package com.openclassrooms.realestatemanager.data.local.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.os.strictmode.ContentUriWithoutPermissionViolation;

import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import com.openclassrooms.realestatemanager.data.local.database.RealEstateDatabase;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
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
    @Test
    public void query() {
        final Cursor cursor = mContentResolver.query(ContentUris.withAppendedId(RealEstatePropertiesProvider.URI_DB,PROPERTY_ID),null,null,null);
        assertNotNull(cursor);
        assertThat(cursor.getCount(), is(3));
        cursor.close();
    }

//    @Test
//    public void getType() {
//    }
}