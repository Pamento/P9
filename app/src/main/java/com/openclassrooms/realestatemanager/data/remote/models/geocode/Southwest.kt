package com.openclassrooms.realestatemanager.data.remote.models.geocode

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class Southwest {
    @SerializedName("lat")
    @Expose
    var lat: Double? = null

    @SerializedName("lng")
    @Expose
    var lng: Double? = null
}