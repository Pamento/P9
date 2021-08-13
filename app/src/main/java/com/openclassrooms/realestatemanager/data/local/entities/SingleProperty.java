package com.openclassrooms.realestatemanager.data.local.entities;

import android.content.ContentValues;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "property",
        indices = @Index(value = "pid"))
public class SingleProperty {

    // autoincrement = false (default)
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "pid")
    private String id;
    private String type;
    private String description;
    private Integer surface;
    private Integer price;
    private Integer rooms;
    private Integer bedroom;
    private Integer bathroom;
    @ColumnInfo(name = "date_register")
    private String dateRegister;
    @ColumnInfo(name = "date_sold")
    private String dateSold;
    @ColumnInfo(name = "address_1")
    private String address1;
    @ColumnInfo(name = "address_2")
    private String address2;
    private String city;
    private String quarter;
    @ColumnInfo(name = "postal_code")
    private Integer postalCode;
    private String location;
    private String amenities;
    private String agent;

    public SingleProperty(@NonNull String id,
                          String type,
                          String description,
                          Integer surface,
                          Integer price,
                          Integer rooms,
                          Integer bedroom,
                          Integer bathroom,
                          String dateRegister,
                          String dateSold,
                          String address1,
                          String address2,
                          String city,
                          String quarter,
                          Integer postalCode,
                          String location,
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
        this.dateRegister = dateRegister;
        this.dateSold = dateSold;
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.quarter = quarter;
        this.postalCode = postalCode;
        this.location = location;
        this.amenities = amenities;
        this.agent = agent;
    }

    @Ignore
    public SingleProperty() {
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

    public String getDateRegister() {
        return dateRegister;
    }

    public void setDateRegister(String dateRegister) {
        this.dateRegister = dateRegister;
    }

    public String getDateSold() {
        return dateSold;
    }

    public void setDateSold(String dateSold) {
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
                ", location='" + location + '\'' +
                ", amenities='" + amenities + '\'' +
                ", agent='" + agent + '\'' +
                '}';
    }

    public static SingleProperty fromContentValues(ContentValues cv) {
        SingleProperty property = new SingleProperty();
        if (cv.containsKey("id")) property.setId(cv.getAsString("id"));
        if (cv.containsKey("type")) property.setType(cv.getAsString("type"));
        if (cv.containsKey("description")) property.setDescription(cv.getAsString("description"));
        if (cv.containsKey("surface")) property.setSurface(cv.getAsInteger("surface"));
        if (cv.containsKey("price")) property.setPrice(cv.getAsInteger("price"));
        if (cv.containsKey("rooms")) property.setRooms(cv.getAsInteger("rooms"));
        if (cv.containsKey("bedroom")) property.setBedroom(cv.getAsInteger("bedroom"));
        if (cv.containsKey("bathroom")) property.setBathroom(cv.getAsInteger("bathroom"));
        if (cv.containsKey("dateRegister"))
            property.setDateRegister(cv.getAsString("dateRegister"));
        if (cv.containsKey("dateSold")) property.setDateSold(cv.getAsString("dateSold"));
        if (cv.containsKey("address1")) property.setAddress1(cv.getAsString("address1"));
        if (cv.containsKey("address2")) property.setAddress2(cv.getAsString("address2"));
        if (cv.containsKey("city")) property.setCity(cv.getAsString("city"));
        if (cv.containsKey("quarter")) property.setQuarter(cv.getAsString("quarter"));
        if (cv.containsKey("postalCode")) property.setPostalCode(cv.getAsInteger("postalCode"));
        if (cv.containsKey("location")) property.setLocation(cv.getAsString("location"));
        if (cv.containsKey("amenities")) property.setAmenities(cv.getAsString("amenities"));
        if (cv.containsKey("agent")) property.setAgent(cv.getAsString("agent"));
        return property;
    }
}
