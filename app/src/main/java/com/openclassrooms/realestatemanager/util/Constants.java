package com.openclassrooms.realestatemanager.util;

import android.Manifest;

public class Constants {

    // GOOGLE SERVICE
    public static final int ERROR_DIALOG_REQUEST = 3881;
    public static final String GOOGLE_BASE_URL = "https://maps.googleapis.com/maps/api/";
    public static final float DEFAULT_MAPS_ZOOM = 15f;
    // Permissions
    public static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int PICK_IMAGE_GALLERY = 2;
    public static final String CHANNEL_ID_1 = "NOTIFICATION_CHANNEL_1";
    public static final String CHANNEL_ID_2 = "NOTIFICATION_CHANNEL_2";
    public static final CharSequence VERBOSE_NOTIFICATION_CHANNEL_1_NAME = "Sey_to_user_the_truth_high_level_with_sound";
    public static final CharSequence VERBOSE_NOTIFICATION_CHANNEL_2_NAME = "Sey_to_user_the_truth_low_level_non_sound";
    public static final String VERBOSE_NOTIFICATION_CHANNEL_1_DESCRIPTION =  "Realtime communication with user of important messages.";
    public static final String VERBOSE_NOTIFICATION_CHANNEL_2_DESCRIPTION =  "Realtime communication with user of state of app work.";

    // Message to show to user
    public static final String SAVE_PROPERTY_OK = "Real Estate was added successfully.";
    public static final String SAVE_PROPERTY_FAIL = "The register of new Real Estate was fail.";
    public static final String SAVE_IMAGES_FAIL = "Some of images does not register. Please, verify it in Detail page and Edit if needed.";

    // TAGs of fragments
    public static final String LIST_FRAGMENT = "AddFragment";
    public static final String MAP_FRAGMENT = "MapFragment";
    public static final String DETAIL_FRAGMENT = "DetailFragment";
    public static final String ADD_FRAGMENT = "AddFragment";
    public static final String EDIT_FRAGMENT = "EditFragment";
    public static final String SEARCH_FRAGMENT = "SearchFragment";
    public static final String SIMULATOR_FRAGMENT = "SimulatorFragment";

    // Amenities
    public static final String SHOP = "Shop";
    public static final String PARK = "Park";
    public static final String PLAYGROUND = "Playground";
    public static final String SCHOOL = "School";
    public static final String BUS = "Bus";
    public static final String SUBWAY = "Subway";
    public static final String NULL = "null";

    // Interest of loan
    public static final double THIRTY_YEAR_RATE = 2.94;
    public static final double TWENTY_YEAR_RATE = 2.72;
    public static final double FIFTEEN_YEAR_RATE = 2.24;
    public static final double THEN_YEAR_RATE = 2.04;
}
