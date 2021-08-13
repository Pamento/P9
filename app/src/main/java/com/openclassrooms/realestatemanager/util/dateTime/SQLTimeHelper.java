package com.openclassrooms.realestatemanager.util.dateTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SQLTimeHelper {

    public static String getUSFormDateFromTimeInMillis(long millisecond) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millisecond);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        return dateFormat.format(calendar.getTime());
    }
}
