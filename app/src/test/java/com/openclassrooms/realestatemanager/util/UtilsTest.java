package com.openclassrooms.realestatemanager.util;

import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

public class UtilsTest {

    private final int value1 = 1000;
    private final int value2 = 1111;
    private final int value3 = 3333;

    @Test
    public void convertDollarToEuro() {
        int euroFromDollars1 = 812;
        int euroFromDollars2 = 902;
        int euroFromDollars3 = 2706;
        int eFd1 = Utils.convertDollarToEuro(value1);
        int eFd2 = Utils.convertDollarToEuro(value2);
        int eFd3 = Utils.convertDollarToEuro(value3);
        assertEquals(euroFromDollars1, eFd1);
        assertEquals(euroFromDollars2, eFd2);
        assertEquals(euroFromDollars3, eFd3);
    }

    @Test
    public void convertEuroToDollar() {
        int dollarFromEuro1 =  1137;
        int dollarFromEuro2 =  1263;
        int dollarFromEuro3 =  3790;
        int eTd1 = Utils.convertEuroToDollar(value1);
        int eTd2 = Utils.convertEuroToDollar(value2);
        int eTd3 = Utils.convertEuroToDollar(value3);
        assertEquals(dollarFromEuro1, eTd1);
        assertEquals(dollarFromEuro2, eTd2);
        assertEquals(dollarFromEuro3, eTd3);
    }

    @Test
    public void getTodayDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String todayDate = dateFormat.format(new Date());

        assertEquals(todayDate, Utils.getTodayDate());
    }

    @Test
    public void getUSFormatOfDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2021);
        calendar.set(Calendar.MONTH, 11);
        calendar.set(Calendar.DAY_OF_MONTH, 2);
        Date date = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String thisDate = dateFormat.format(date);

        assertEquals(thisDate, Utils.getUSFormatOfDate(date));
    }
}