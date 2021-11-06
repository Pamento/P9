package com.openclassrooms.realestatemanager.data.remote.models.geocode

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import com.openclassrooms.realestatemanager.data.remote.models.geocode.Northeast
import com.openclassrooms.realestatemanager.data.remote.models.geocode.Southwest

class Viewport {
    @SerializedName("northeast")
    @Expose
    var northeast: Northeast? = null

    @SerializedName("southwest")
    @Expose
    var southwest: Southwest? = null
}