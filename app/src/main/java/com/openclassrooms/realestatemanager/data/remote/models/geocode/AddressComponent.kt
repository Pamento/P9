package com.openclassrooms.realestatemanager.data.remote.models.geocode

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class AddressComponent {
    @SerializedName("long_name")
    @Expose
    var longName: String? = null

    @SerializedName("short_name")
    @Expose
    var shortName: String? = null

    @SerializedName("types")
    @Expose
    var types: List<String>? = null
}