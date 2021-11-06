package com.openclassrooms.realestatemanager.data.remote.models.geocode

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class PlusCode {
    @SerializedName("compound_code")
    @Expose
    var compoundCode: String? = null

    @SerializedName("global_code")
    @Expose
    var globalCode: String? = null
}