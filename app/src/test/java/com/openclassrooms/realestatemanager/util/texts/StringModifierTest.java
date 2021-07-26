package com.openclassrooms.realestatemanager.util.texts;

import org.junit.Test;

import static org.junit.Assert.*;

public class StringModifierTest {

    final String[] arrayString = {"one", "null", "three", "four", "null", "null"};
    final String oneString = "one-null-three-four-null-null-";
    final String[] arrayStringWithoutNullTextString = {"one", "three", "four"};
    final String strToTest = "1234567";
    final String strToTestResult = "1,234,567";

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
}