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
    public Integer id;
    public String path;
    public String description;
    @ColumnInfo(name = "property_id")
    public String propertyId;

    public ImageOfProperty(@Nullable Integer id, @NonNull String path, String description, @NonNull String propertyId) {
        this.id = id;
        this.path = path;
        this.description = description;
        this.propertyId = propertyId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    @Override
    public String toString() {
        return "ImageOfProperty{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", description='" + description + '\'' +
                ", propertyId='" + propertyId + '\'' +
                '}';
    }
}
