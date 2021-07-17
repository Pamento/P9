package com.openclassrooms.realestatemanager.util.resources;

public abstract class AppResources {

    private static final String[] PROPERTY_TYPE = new String[] {
            "Duplex",
            "Flat",
            "House",
            "Loft",
            "Penthouse",
            "Townhouse",
            "Triplex",
            "Villa"
    };

    public static String[] getPropertyType() {
        return PROPERTY_TYPE;
    }

    private static final String[] AGENTS = new String[] {
            "Mickael Stanford",
            "Tonny Macmillan",
            "Veronica Hilton",
            "Tom Nicky",
            "Murphy Cooper",
            "Jimmy Carter"
    };

    public static String[] getAGENTS() {
        return AGENTS;
    }
}
