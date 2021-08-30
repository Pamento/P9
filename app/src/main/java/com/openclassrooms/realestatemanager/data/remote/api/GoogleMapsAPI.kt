package com.openclassrooms.realestatemanager.data.remote.api

import com.openclassrooms.realestatemanager.BuildConfig
import retrofit2.http.GET
import com.openclassrooms.realestatemanager.data.remote.models.geocode.GeoResponse
import io.reactivex.Observable
import retrofit2.http.Query

interface GoogleMapsAPI {
    companion object {
        const val KEY = BuildConfig.MAPS_API_KEY
    }

    @GET("geocode/json?region=us&key=$KEY")
    fun getLocationOfAddress(@Query("address") address: String?): Observable<GeoResponse?>?
}