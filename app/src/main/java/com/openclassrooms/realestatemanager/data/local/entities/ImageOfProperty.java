package com.openclassrooms.realestatemanager.data.local.entities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "property_image",
        indices = @Index(value = "property_id"),
        foreignKeys = {@ForeignKey(entity = SingleProperty.class,
                parentColumns = "pid",
                childColumns = "property_id",
                onDelete = ForeignKey.CASCADE)
        })
public class ImageOfProperty {

    @PrimaryKey(autoGenerate = true)
    public Long id;
    public String path;
    public String description;
    @ColumnInfo(name = "property_id")
    public String propertyId;

    public ImageOfProperty(@Nullable Long id, @NonNull String path, String description, @NonNull String propertyId) {
        this.id = id;
        this.path = path;
        this.description = description;
        this.propertyId = propertyId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }
}
