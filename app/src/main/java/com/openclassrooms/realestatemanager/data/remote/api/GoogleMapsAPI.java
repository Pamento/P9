package com.openclassrooms.realestatemanager.data.remote.api;

import com.openclassrooms.realestatemanager.BuildConfig;
import com.openclassrooms.realestatemanager.data.remote.models.geocode.GeoResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Best practice to code un URL correctly spelled
 * https://developers.google.com/maps/documentation/maps-static/static-web-api-best-practices#BuildingURLs
 * ex:
 * address=24%20Sussex%20Drive%20Ottawa%20ON
 */
public interface GoogleMapsAPI {

    String KEY = BuildConfig.MAPS_API_KEY;

    /**
     *
     * @param address
     * @return json https://developers.google.com/maps/documentation/geocoding/overview#GeocodingResponses
     * ex:
     * region=es for Spain
     * https://maps.googleapis.com/maps/api/geocode/json?address=Toledo&region=es&key=YOUR_API_KEY
     * ex:
     *     //https://maps.googleapis.com/maps/api/geocode/outputFormat?parameters
     */
    @GET("geocode/json?region=us" + KEY)
    Observable<GeoResponse> getLocationOfAddress(@Query("address") String address);

    /*
     * example with EiffelTower and red marker
     * String latEiffelTower = "48.858235";
     * String lngEiffelTower = "2.294571";
     String url = "https://maps.google.com/maps/api/staticmap?center=" + latEiffelTower + "," + lngEiffelTower + "&zoom=15&size=200x200&markers=size:mid%7Ccolor:red%7C" + latEiffelTower + "," + lngEiffelTower + "&key=YOUR_API_KEY"
     * ex: markers
     * https://maps.googleapis.com/maps/api/staticmap?size=200x200&zoom=15
     * &markers=size:mid%7Ccolor:red%7CSan+Francisco,CA%7COakland,CA%7CSan+Jose,CA&key=YOUR_API_KEY
     * https://maps.google.com/maps/api/staticmap?center=%2248.858235,2.294571%22&zoom=15&size=200x200&markers=size:mid%7Ccolor:red%7C%2248.858235,2.294571%22&key=
     * ex:
     * https://maps.googleapis.com/maps/api/staticmap?center=Berkeley,CA&zoom=14&size=400x400&key=YOUR_API_KEY
     *
     */
}
