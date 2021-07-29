package com.openclassrooms.realestatemanager.data.remote.repository;

import com.openclassrooms.realestatemanager.data.remote.api.GoogleMapsAPI;
import com.openclassrooms.realestatemanager.data.remote.client.RetrofitClient;
import com.openclassrooms.realestatemanager.data.remote.models.geocode.GeoResponse;
import com.openclassrooms.realestatemanager.data.remote.models.geocode.Result;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GoogleMapsRepository {

    private static volatile GoogleMapsRepository INSTANCE;
    private final GoogleMapsAPI mGoogleMapsAPI;

    public GoogleMapsRepository() {
        mGoogleMapsAPI = getGoogleMapsAPI();
    }

    private GoogleMapsAPI getGoogleMapsAPI() {
        return RetrofitClient.getRequestApi();
    }

    public static GoogleMapsRepository getInstance() {
        if (INSTANCE == null) INSTANCE = new GoogleMapsRepository();
        return INSTANCE;
    }

    public Observable<GeoResponse> streamGoogleGeoCode(String address) {
        return mGoogleMapsAPI.getLocationOfAddress(address)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public Observable<Result> getGoogleGeoCodeOfAddress(String address) {
        return streamGoogleGeoCode(address)
                .map(geoResponse -> {
                    String success = geoResponse.getStatus();
                    if (success.equals("OK")) {
                        return geoResponse.getResults().get(0);
                    }
                    return null;
                });
    }
}
