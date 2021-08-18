package com.openclassrooms.realestatemanager.data.remote.api;

import com.openclassrooms.realestatemanager.BuildConfig;
import com.openclassrooms.realestatemanager.data.remote.models.geocode.GeoResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleMapsAPI {

    String KEY = BuildConfig.MAPS_API_KEY;

    @GET("geocode/json?region=us&key=" + KEY)
    Observable<GeoResponse> getLocationOfAddress(@Query("address") String address);
}
