package com.openclassrooms.realestatemanager.util.dateTime;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.*;

public class SQLTimeHelperTest {

    @Test
    public void getUSFormDateFromTimeInMillis() {
        long timeInMilliseconds = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2021);
        calendar.set(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 2);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        String thisDate = dateFormat.format(timeInMilliseconds);
        assertEquals(thisDate, SQLTimeHelper.getUSFormDateFromTimeInMillis(timeInMilliseconds));
    }
}