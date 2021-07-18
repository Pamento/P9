package com.openclassrooms.realestatemanager.data.local.entities;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;


public class PropertyWithImages {
    @Embedded public SingleProperty mSingleProperty;
    @Relation(
            parentColumn = "pid",
            entityColumn = "property_id"
    )
    public List<ImageOfProperty> ImagesOfProperty;
}
