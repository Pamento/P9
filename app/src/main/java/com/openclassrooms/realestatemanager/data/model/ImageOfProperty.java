package com.openclassrooms.realestatemanager.data.model;

import androidx.room.PrimaryKey;

public class ImageOfProperty {

    @PrimaryKey
    String id;
    String path;
    String description;
    String propertyId;

    public ImageOfProperty(String id, String path, String description, String propertyId) {
        this.id = id;
        this.path = path;
        this.description = description;
        this.propertyId = propertyId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
