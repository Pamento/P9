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

    public SingleProperty(String id,
                          String type,
                          String description,
                          int surface,
                          long price,
                          int rooms,
                          int bedroom,
                          int bathroom,
                          long dateInit,
                          long dateSold,
                          String address1,
                          String address2,
                          String city,
                          String quarter,
                          int postalCode,
                          String amenities,
                          String agent) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.surface = surface;
        this.price = price;
        this.rooms = rooms;
        this.bedroom = bedroom;
        this.bathroom = bathroom;
        this.dateInit = dateInit;
        this.dateSold = dateSold;
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.quarter = quarter;
        this.postalCode = postalCode;
        this.amenities = amenities;
        this.agent = agent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSurface() {
        return surface;
    }

    public void setSurface(int surface) {
        this.surface = surface;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public int getRooms() {
        return rooms;
    }

    public void setRooms(int rooms) {
        this.rooms = rooms;
    }

    public int getBedroom() {
        return bedroom;
    }

    public void setBedroom(int bedroom) {
        this.bedroom = bedroom;
    }

    public int getBathroom() {
        return bathroom;
    }

    public void setBathroom(int bathroom) {
        this.bathroom = bathroom;
    }

    public long getDateInit() {
        return dateInit;
    }

    public void setDateInit(long dateInit) {
        this.dateInit = dateInit;
    }

    public long getDateSold() {
        return dateSold;
    }

    public void setDateSold(long dateSold) {
        this.dateSold = dateSold;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getQuarter() {
        return quarter;
    }

    public void setQuarter(String quarter) {
        this.quarter = quarter;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }
}
