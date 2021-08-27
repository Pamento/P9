package com.openclassrooms.realestatemanager.data.local.models

data class RowQueryEstates(
    var type: String = "",
    var minSurface: Int = 0,
    var maxSurface: Int = 0,
    var minPrice: Int = 0,
    var maxPrice: Int = 0,
    var rooms: Int = 0,
    var bedroom: Int = 0,
    var bathroom: Int = 0,
    var quarter: String = "",
    var dateRegister: String = "0",
    var isSoldEstateInclude: Boolean = false,
)
