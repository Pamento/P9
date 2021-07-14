package com.openclassrooms.realestatemanager.data.model;


import androidx.room.PrimaryKey;

public class SingleProperty {

    // Not autoincrement
    @PrimaryKey
    public String id;
    public String type;
    public String description;
    public int surface;
    public long price;
    public int rooms;
    public int bedroom;
    public int bathroom;
    public long dateInit;
    public long dateSold;
    public String address1;
    public String address2;
    public String city;
    public String quarter;
    public int postalCode;
    public String amenities;
    public String agent;


    public SingleProperty(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
