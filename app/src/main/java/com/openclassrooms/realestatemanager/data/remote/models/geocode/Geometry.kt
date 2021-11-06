package com.openclassrooms.realestatemanager.data.remote.models.geocode

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class Geometry {
    @SerializedName("location")
    @Expose
    var location: Location? = null

    @SerializedName("location_type")
    @Expose
    var locationType: String? = null

    @SerializedName("viewport")
    @Expose
    var viewport: Viewport? = null
}