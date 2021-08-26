package com.openclassrooms.realestatemanager.data.local.models

data class RowQueryEstates(
    var type: String? = null,
    var minSurface: Int? = null,
    var maxSurface: Int? = null,
    var minPrice: Int? = null,
    var maxPrice: Int? = null,
    var rooms: Int? = null,
    var bedroom: Int? = null,
    var bathroom: Int? = null,
    var quarter: String? = null,
    var dateRegister: String? = null,
    var isSoldEstateInclude: Boolean = false,
)
