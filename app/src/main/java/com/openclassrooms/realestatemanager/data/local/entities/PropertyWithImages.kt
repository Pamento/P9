package com.openclassrooms.realestatemanager.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation

class PropertyWithImages {
    @JvmField
    @Embedded
    var mSingleProperty: SingleProperty? = null

    @JvmField
    @Relation(parentColumn = "pid", entityColumn = "property_id")
    var ImagesOfProperty: List<ImageOfProperty>? = null
}