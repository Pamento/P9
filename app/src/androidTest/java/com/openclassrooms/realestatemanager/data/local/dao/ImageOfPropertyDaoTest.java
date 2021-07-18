package com.openclassrooms.realestatemanager.data.local.dao;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import com.openclassrooms.realestatemanager.data.local.database.RealEstateDatabase;
import com.openclassrooms.realestatemanager.data.local.entities.ImageOfProperty;
import com.openclassrooms.realestatemanager.data.local.entities.SingleProperty;
import com.openclassrooms.realestatemanager.utils.LiveDataTestUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ImageOfPropertyDaoTest {

    private volatile RealEstateDatabase mRealEstateDatabase;
    private static final Integer IMAGE_ID = 1;
    private static final String PROPERTY_ID = "property_id_1";
    private static final String PROPERTY_ID_TWO = "property_id_2";
    private static final SingleProperty SINGLE_PROPERTY_ONE = new SingleProperty("property_id_1",null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
    private static final SingleProperty SINGLE_PROPERTY_TWO = new SingleProperty("property_id_2",null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
    private static final ImageOfProperty IMAGE_ONE = new ImageOfProperty(1,"path_to_image", "image one description", "property_id_1");
    private static final ImageOfProperty IMAGE_TWO = new ImageOfProperty(2,"path_to_image", "image two description", "property_id_1");
    private static final ImageOfProperty IMAGE_THREE = new ImageOfProperty(3,"path_to_image", "image three description", "property_id_2");
    private static final ImageOfProperty IMAGE_FOUR = new ImageOfProperty(4,"path_to_image", "image four description", "property_id_2");

    @Rule
    public InstantTaskExecutorRule mInstantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() throws Exception {
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        mRealEstateDatabase = Room.inMemoryDatabaseBuilder(context,RealEstateDatabase.class)
                .allowMainThreadQueries()
                .build();
        mRealEstateDatabase.singlePropertyDao().createSingleProperty(SINGLE_PROPERTY_ONE);
        mRealEstateDatabase.singlePropertyDao().createSingleProperty(SINGLE_PROPERTY_TWO);
    }

    @After
    public void tearDown() throws Exception {
        mRealEstateDatabase.close();
    }

    @Test
    public void insertImagesOfProperty() throws InterruptedException {
        mRealEstateDatabase.imageOfPropertyDao().insertImageOfProperty(IMAGE_ONE);
        mRealEstateDatabase.imageOfPropertyDao().insertImageOfProperty(IMAGE_TWO);
        mRealEstateDatabase.imageOfPropertyDao().insertImageOfProperty(IMAGE_THREE);
        List<ImageOfProperty> imagesOfProperty = LiveDataTestUtils.getValue(mRealEstateDatabase.imageOfPropertyDao().getAllImageForProperty(PROPERTY_ID));
        assertTrue(imagesOfProperty.get(0).getDescription().equals(IMAGE_ONE.getDescription()) && imagesOfProperty.get(0).getPropertyId().equals(PROPERTY_ID));
        assertEquals(2, imagesOfProperty.size());
    }

    @Test
    public void insertImageOfProperty() throws InterruptedException {
        mRealEstateDatabase.imageOfPropertyDao().insertImageOfProperty(IMAGE_ONE);
        mRealEstateDatabase.imageOfPropertyDao().insertImageOfProperty(IMAGE_TWO);
        mRealEstateDatabase.imageOfPropertyDao().insertImageOfProperty(IMAGE_THREE);
        mRealEstateDatabase.imageOfPropertyDao().insertImageOfProperty(IMAGE_FOUR);
        List<ImageOfProperty> imagesOfProperty = LiveDataTestUtils.getValue(mRealEstateDatabase.imageOfPropertyDao().getAllImageForProperty(PROPERTY_ID_TWO));
        assertTrue(imagesOfProperty.get(1).getDescription().equals(IMAGE_FOUR.getDescription()) && imagesOfProperty.get(1).getPropertyId().equals(PROPERTY_ID_TWO));
        assertEquals(2, imagesOfProperty.size());
    }

    @Test
    public void getAllImageForProperty() throws InterruptedException {
        mRealEstateDatabase.imageOfPropertyDao().insertImageOfProperty(IMAGE_ONE);
        mRealEstateDatabase.imageOfPropertyDao().insertImageOfProperty(IMAGE_TWO);
        mRealEstateDatabase.imageOfPropertyDao().insertImageOfProperty(IMAGE_THREE);
        mRealEstateDatabase.imageOfPropertyDao().insertImageOfProperty(IMAGE_FOUR);
        List<ImageOfProperty> imagesOfProperty = LiveDataTestUtils.getValue(mRealEstateDatabase.imageOfPropertyDao().getAllImageForProperty(PROPERTY_ID_TWO));
        assertEquals(2, imagesOfProperty.size());
    }

    @Test
    public void deleteImage() throws InterruptedException {
        mRealEstateDatabase.imageOfPropertyDao().insertImageOfProperty(IMAGE_ONE);
        List<ImageOfProperty> imagesOfProperty = LiveDataTestUtils.getValue(mRealEstateDatabase.imageOfPropertyDao().getAllImageForProperty(PROPERTY_ID));
        assertEquals(1, imagesOfProperty.size());
        mRealEstateDatabase.imageOfPropertyDao().deleteImage(IMAGE_ID);
        List<ImageOfProperty> imagesOfProperty2 = LiveDataTestUtils.getValue(mRealEstateDatabase.imageOfPropertyDao().getAllImageForProperty(PROPERTY_ID));
        assertEquals(0, imagesOfProperty2.size());
    }
}