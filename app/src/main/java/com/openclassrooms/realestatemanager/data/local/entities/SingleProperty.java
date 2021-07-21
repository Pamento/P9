package com.openclassrooms.realestatemanager.data.local.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "property",
        indices = @Index(value = "pid"))
public class SingleProperty {

    // autoincrement = false (default)
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "pid")
    public String id;
    public String type;
    public String description;
    public Integer surface;
    public Integer price;
    public Integer rooms;
    public Integer bedroom;
    public Integer bathroom;
    @ColumnInfo(name = "date_register")
    public Integer dateRegister;
    @ColumnInfo(name = "date_sold")
    public Integer dateSold;
    @ColumnInfo(name = "address_1")
    public String address1;
    @ColumnInfo(name = "address_2")
    public String address2;
    public String city;
    public String quarter;
    @ColumnInfo(name = "postal_code")
    public Integer postalCode;
    public String amenities;
    public String agent;

    public SingleProperty() {/**/}

    public SingleProperty(@NonNull String id,
                          String type,
                          String description,
                          Integer surface,
                          Integer price,
                          Integer rooms,
                          Integer bedroom,
                          Integer bathroom,
                          Integer dateInit,
                          Integer dateSold,
                          String address1,
                          String address2,
                          String city,
                          String quarter,
                          Integer postalCode,
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
        this.dateRegister = dateInit;
        this.dateSold = dateSold;
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.quarter = quarter;
        this.postalCode = postalCode;
        this.amenities = amenities;
        this.agent = agent;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
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

    public Integer getSurface() {
        return surface;
    }

    public void setSurface(Integer surface) {
        this.surface = surface;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getRooms() {
        return rooms;
    }

    public void setRooms(Integer rooms) {
        this.rooms = rooms;
    }

    public Integer getBedroom() {
        return bedroom;
    }

    public void setBedroom(Integer bedroom) {
        this.bedroom = bedroom;
    }

    public Integer getBathroom() {
        return bathroom;
    }

    public void setBathroom(Integer bathroom) {
        this.bathroom = bathroom;
    }

    public Integer getDateRegister() {
        return dateRegister;
    }

    public void setDateRegister(Integer dateRegister) {
        this.dateRegister = dateRegister;
    }

    public Integer getDateSold() {
        return dateSold;
    }

    public void setDateSold(Integer dateSold) {
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

    public Integer getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(Integer postalCode) {
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

    @Override
    public String toString() {
        return "SingleProperty{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", surface=" + surface +
                ", price=" + price +
                ", rooms=" + rooms +
                ", bedroom=" + bedroom +
                ", bathroom=" + bathroom +
                ", dateRegister=" + dateRegister +
                ", dateSold=" + dateSold +
                ", address1='" + address1 + '\'' +
                ", address2='" + address2 + '\'' +
                ", city='" + city + '\'' +
                ", quarter='" + quarter + '\'' +
                ", postalCode=" + postalCode +
                ", amenities='" + amenities + '\'' +
                ", agent='" + agent + '\'' +
                '}';
    }
}
