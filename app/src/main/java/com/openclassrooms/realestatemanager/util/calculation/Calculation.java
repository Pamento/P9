package com.openclassrooms.realestatemanager.util.calculation;

import java.text.DecimalFormat;

public class Calculation {

    public static String calculateMonthlyLoan(int amount, int contribution, double interest, int duration) {
        double result = (amount - contribution + ((amount - contribution) * (interest / 100))) / duration;
        String resultInString = new DecimalFormat("##.##").format(result);
        if (resultInString.contains(",")) {
            String[] strings = resultInString.split(",");
            if (strings[1].length() == 1) {
                resultInString += "0";
            }
        }
        return resultInString;
    }
}
