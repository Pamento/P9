package com.openclassrooms.realestatemanager.util.calculation;

import org.junit.Test;

import static com.openclassrooms.realestatemanager.util.Constants.Constants.THIRTY_YEAR_RATE;
import static org.junit.Assert.*;

public class CalculationTest {

    private final String result1 = "4289,17";
    private final String result2 = "5575,92";
    private final String result3 = "1429,72";
    private final String result4 = "1029,40";

    private final int amount1 = 1500000;
    private final int amount2 = 1300000;
    private final int amount3 = 250000;
    private final int amount4 = 120000;

    private final int month1 = 360;
    private final int month2 = 240;
    private final int month3 = 180;
    private final int month4 = 120;

    @Test
    public void calculateMonthlyLoan() {
        String calculation1 = Calculation.calculateMonthlyLoan(amount1, 0, THIRTY_YEAR_RATE, month1);
        assertEquals(result1, calculation1);
        String calculation2 = Calculation.calculateMonthlyLoan(amount2, 0, THIRTY_YEAR_RATE, month2);
        assertEquals(result2, calculation2);
        String calculation3 = Calculation.calculateMonthlyLoan(amount3, 0, THIRTY_YEAR_RATE, month3);
        assertEquals(result3, calculation3);
        String calculation4 = Calculation.calculateMonthlyLoan(amount4, 0, THIRTY_YEAR_RATE, month4);
        assertEquals(result4, calculation4);
    }
}