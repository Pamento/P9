package com.openclassrooms.realestatemanager.data.local.entities

import androidx.room.*

@Entity(
    tableName = "property_image",
    indices = [Index(value = ["property_id"])],
    foreignKeys = [ForeignKey(
        entity = SingleProperty::class,
        parentColumns = ["pid"],
        childColumns = ["property_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
class ImageOfProperty(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var path: String = "",
    var description: String = "",

    @ColumnInfo(name = "property_id")
    var propertyId: String = "",
    )