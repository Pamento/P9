package com.openclassrooms.realestatemanager.util.texts;

import org.junit.Test;

import static org.junit.Assert.*;

public class StringModifierTest {

    final String[] arrayString = {"one", "null", "three", "four", "null", "null"};
    final String oneString = "one-null-three-four-null-null-";
    final String[] arrayStringWithoutNullTextString = {"one", "three", "four"};
    final String strToTest = "1234567";
    final String strToTestResult = "1,234,567";
    final String address1 = "127 Prince St";
    final String city = "New York";
    final String quarter = "Manhattan";
    final String stringJoinByPlus = "127+Prince+St";
    final String formattedAddress = "127+Prince+St,Manhattan,New+York";

    @Test
    public void arrayToSingleString() {
        String simpleString = StringModifier.arrayToSingleString(arrayString);
        assertEquals(oneString, simpleString);
    }

    @Test
    public void singleStringToArrayString() {
        String[] convertedStingToArray = StringModifier.singleStringToArrayString(oneString);
        assertArrayEquals(arrayStringWithoutNullTextString, convertedStingToArray);
    }

    @Test
    public void addComaInPriceTest() {
        String res = StringModifier.addComaInPrice(strToTest);
        assertEquals(strToTestResult, res);
    }

    @Test
    public void formatAddressToGeocoding() {
        String s = StringModifier.formatAddressToGeocoding(address1,city,quarter);
        assertEquals(formattedAddress, s);
    }

    @Test
    public void replaceWhitespaceWithPlus() {
        String s = StringModifier.replaceWhitespaceWithPlus(address1);
        assertEquals(stringJoinByPlus, s);
    }
}