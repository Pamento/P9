package com.openclassrooms.realestatemanager.data.local.entities

import androidx.room.*

@Entity(tableName = "property", indices = [Index(value = arrayOf("pid"))])
data class SingleProperty(
    @PrimaryKey
    @ColumnInfo(name = "pid")
    var id: String = "",
    var type: String = "",
    var description: String = "",
    var surface: Int = 0,
    var price: Int = 0,
    var rooms: Int = 0,
    var bedroom: Int = 0,
    var bathroom: Int = 0,

    @ColumnInfo(name = "date_register")
    var dateRegister: String = "",

    @ColumnInfo(name = "date_sold")
    var dateSold: String = "",

    @ColumnInfo(name = "address_1")
    var address1: String = "",

    @ColumnInfo(name = "address_2")
    var address2: String = "",
    var city: String = "",
    var quarter: String = "",

    @ColumnInfo(name = "postal_code")
    var postalCode: Int = 0,
    var location: String = "",
    var amenities: String = "",
    var agent: String = "",
)