package com.openclassrooms.realestatemanager.util.resources;

import static com.openclassrooms.realestatemanager.util.Constants.Constants.FIFTEEN_YEAR_RATE;
import static com.openclassrooms.realestatemanager.util.Constants.Constants.THEN_YEAR_RATE;
import static com.openclassrooms.realestatemanager.util.Constants.Constants.THIRTY_YEAR_RATE;
import static com.openclassrooms.realestatemanager.util.Constants.Constants.TWENTY_YEAR_RATE;

public abstract class AppResources {

    private static final String[] PROPERTY_TYPE = new String[]{
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

    private static final String[] AGENTS = new String[]{
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

    public static Double getInterestFixedRate(int months) {
        if (months == 0) return THIRTY_YEAR_RATE;
        else if (months >= 360) return THIRTY_YEAR_RATE;
        else if (months >= 240) return TWENTY_YEAR_RATE;
        else if (months >= 180) return FIFTEEN_YEAR_RATE;
        else return THEN_YEAR_RATE;
    }

    public static Double getInterestFixedRate(String interest) {
        switch (interest) {
            case "2.94":
                return THIRTY_YEAR_RATE;
            case "2.72":
                return TWENTY_YEAR_RATE;
            case "2.24":
                return FIFTEEN_YEAR_RATE;
            case "2.04":
                return THEN_YEAR_RATE;
            default:
                return null;
        }
    }
}
