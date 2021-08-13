package com.openclassrooms.realestatemanager.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowNetwork;
import org.robolectric.shadows.ShadowNetworkInfo;

import static org.robolectric.Shadows.shadowOf;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class UtilsConnectivityTest {

    private ConnectivityManager connectivityManager;
    private ShadowNetworkInfo mShadowNetworkInfo;
    private ShadowNetwork mShadowNetwork;
    private Context context;

    @Before
    public void setUp() throws Exception {
        connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        mShadowNetworkInfo = shadowOf(connectivityManager.getActiveNetworkInfo());
        mShadowNetwork = shadowOf(connectivityManager.getActiveNetwork());
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void isInternetAvailable_true() {
        mShadowNetworkInfo.setConnectionStatus(NetworkInfo.State.CONNECTED);
        assertTrue(Utils.isInternetAvailable());
    }

    @Test
    public void isInternetAvailable_false() {
    }
}