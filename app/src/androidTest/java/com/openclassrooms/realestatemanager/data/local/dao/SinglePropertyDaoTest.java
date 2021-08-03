package com.openclassrooms.realestatemanager.data.local.dao;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import com.openclassrooms.realestatemanager.data.local.database.RealEstateDatabase;
import com.openclassrooms.realestatemanager.data.local.entities.ImageOfProperty;
import com.openclassrooms.realestatemanager.data.local.entities.PropertyWithImages;
import com.openclassrooms.realestatemanager.data.local.entities.SingleProperty;
import com.openclassrooms.realestatemanager.utils.LiveDataTestUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class SinglePropertyDaoTest {

    private volatile RealEstateDatabase mRealEstateDatabase;
    private static final String PROPERTY_ID = "property_id_1";
    private static final String PROPERTY_DESCRIPTION_ONE = "Description flat";
    private static final String PROPERTY_DESCRIPTION_TWO = "Description house";
    private static final SingleProperty SINGLE_PROPERTY_ONE = new SingleProperty("property_id_1", "Flat", "Description flat", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    private static final SingleProperty SINGLE_PROPERTY_TWO = new SingleProperty("property_id_1", "House", "Description house", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    private static final ImageOfProperty IMAGE_ONE = new ImageOfProperty(1, "path_to_image", "image one description", "property_id_1");
    private static final ImageOfProperty IMAGE_TWO = new ImageOfProperty(2, "path_to_image", "image two description", "property_id_1");


    @Rule
    public InstantTaskExecutorRule mInstantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() throws Exception {
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        mRealEstateDatabase = Room.inMemoryDatabaseBuilder(context, RealEstateDatabase.class)
                .allowMainThreadQueries()
                .build();
    }

    @After
    public void tearDown() throws Exception {
        mRealEstateDatabase.close();
    }

    @Test
    public void createSingleProperty() throws InterruptedException {
        mRealEstateDatabase.singlePropertyDao().createSingleProperty(SINGLE_PROPERTY_ONE);
        SingleProperty singleProperty = LiveDataTestUtils.getValue(mRealEstateDatabase.singlePropertyDao().getSingleProperty(PROPERTY_ID));
        assertNotNull(singleProperty);
    }

    @Test
    public void updateProperty() throws InterruptedException {
        mRealEstateDatabase.singlePropertyDao().createSingleProperty(SINGLE_PROPERTY_ONE);
        SingleProperty singleProperty = LiveDataTestUtils.getValue(mRealEstateDatabase.singlePropertyDao().getSingleProperty(PROPERTY_ID));
        assertTrue(singleProperty.getType() != null && singleProperty.getDescription().equals(PROPERTY_DESCRIPTION_ONE));
        mRealEstateDatabase.singlePropertyDao().updateProperty(SINGLE_PROPERTY_TWO);
        SingleProperty singlePropertyUpdated = LiveDataTestUtils.getValue(mRealEstateDatabase.singlePropertyDao().getSingleProperty(PROPERTY_ID));
        assertTrue(singlePropertyUpdated.getType() != null && singlePropertyUpdated.getDescription().equals(PROPERTY_DESCRIPTION_TWO));
    }

    @Test
    public void getSingleProperty() throws InterruptedException {
        SingleProperty singleProperty1 = LiveDataTestUtils.getValue(mRealEstateDatabase.singlePropertyDao().getSingleProperty(PROPERTY_ID));
        assertNull(singleProperty1);
        mRealEstateDatabase.singlePropertyDao().createSingleProperty(SINGLE_PROPERTY_ONE);
        SingleProperty singleProperty2 = LiveDataTestUtils.getValue(mRealEstateDatabase.singlePropertyDao().getSingleProperty(PROPERTY_ID));
        assertNotNull(singleProperty2);
    }

    @Test
    public void getPropertyWithImages() throws InterruptedException {
        mRealEstateDatabase.singlePropertyDao().createSingleProperty(SINGLE_PROPERTY_ONE);
        mRealEstateDatabase.imageOfPropertyDao().insertImageOfProperty(IMAGE_ONE);
        mRealEstateDatabase.imageOfPropertyDao().insertImageOfProperty(IMAGE_TWO);
        List<PropertyWithImages> propertyWithImages = LiveDataTestUtils.getValue(mRealEstateDatabase.singlePropertyDao().getPropertyWithImages());
        assertNotNull(propertyWithImages);
        assertNotNull(propertyWithImages.get(0).ImagesOfProperty.get(0));
    }
}