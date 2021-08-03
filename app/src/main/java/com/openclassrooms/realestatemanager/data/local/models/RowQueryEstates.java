package com.openclassrooms.realestatemanager.data.local.models;

public class RowQueryEstates {

    private String type;
    private Integer minSurface;
    private Integer maxSurface;
    private Integer minPrice;
    private Integer maxPrice;
    private Integer rooms;
    private Integer bedroom;
    private Integer bathroom;
    private String quarter;
    private String dateRegister;
    private boolean isSoldEstateInclude;

    public RowQueryEstates() {/**/}

    public RowQueryEstates(String type,
                           Integer minSurface,
                           Integer maxSurface,
                           Integer minPrice,
                           Integer maxPrice,
                           Integer rooms,
                           Integer bedroom,
                           Integer bathroom,
                           String quarter,
                           String dateRegister,
                           boolean isSoldEstateInclude) {
        this.type = type;
        this.minSurface = minSurface;
        this.maxSurface = maxSurface;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.rooms = rooms;
        this.bedroom = bedroom;
        this.bathroom = bathroom;
        this.quarter = quarter;
        this.dateRegister = dateRegister;
        this.isSoldEstateInclude = isSoldEstateInclude;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getMinSurface() {
        return minSurface;
    }

    public void setMinSurface(Integer minSurface) {
        this.minSurface = minSurface;
    }

    public Integer getMaxSurface() {
        return maxSurface;
    }

    public void setMaxSurface(Integer maxSurface) {
        this.maxSurface = maxSurface;
    }

    public Integer getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Integer minPrice) {
        this.minPrice = minPrice;
    }

    public Integer getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Integer maxPrice) {
        this.maxPrice = maxPrice;
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

    public String getQuarter() {
        return quarter;
    }

    public void setQuarter(String quarter) {
        this.quarter = quarter;
    }

    public String getDateRegister() {
        return dateRegister;
    }

    public void setDateRegister(String dateRegister) {
        this.dateRegister = dateRegister;
    }

    public boolean isSoldEstateInclude() {
        return isSoldEstateInclude;
    }

    public void setSoldEstateInclude(boolean soldEstateInclude) {
        isSoldEstateInclude = soldEstateInclude;
    }

    @Override
    public String toString() {
        return "RowQueryEstates{" +
                "type='" + type + '\'' +
                ", minSurface=" + minSurface +
                ", maxSurface=" + maxSurface +
                ", minPrice=" + minPrice +
                ", maxPrice=" + maxPrice +
                ", rooms=" + rooms +
                ", bedroom=" + bedroom +
                ", bathroom=" + bathroom +
                ", quarter='" + quarter + '\'' +
                ", dateRegister='" + dateRegister + '\'' +
                ", isSoldEstateInclude=" + isSoldEstateInclude +
                '}';
    }
}
