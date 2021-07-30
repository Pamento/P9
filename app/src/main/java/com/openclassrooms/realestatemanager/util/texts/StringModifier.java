package com.openclassrooms.realestatemanager.util.texts;

import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;

public class StringModifier {

    public static String arrayToSingleString(String[] s) {
        StringBuilder strBuilder = new StringBuilder();
        for (String str: s) {
            strBuilder.append(str).append("-");
        }
        return strBuilder.toString();
    }

    public static String[] singleStringToArrayString(String s) {
        String[] items = s.split("\\s*-\\s*");
        List<String> newStringList = new ArrayList<>();
        for (String ns : items) {
            if (!ns.equals("null")) newStringList.add(ns);
        }
        return newStringList.toArray(new String[0]);
    }

    public static String addComaInPrice(String priceToAddComa) {
        //final String strToTest = "1234567";
        String[] s = priceToAddComa.split("");
        int r = s.length % 3;
        StringBuilder comaPrice = new StringBuilder();
        for (String irs: s) {
            if (r == 0 && (comaPrice.length() == 0)) {
                comaPrice.append(irs);
                r = 2;
            } else if (r == 0 && comaPrice.length() != (s.length -1)) {
                comaPrice.append(",").append(irs);
                r = 2;
            } else {
                comaPrice.append(irs);
                r--;
            }
        }
        return comaPrice.toString();
    }

    public static String formatAddressToGeocoding(String address1, String city, String quarter) {
        return replaceWhitespaceWithPlus(address1) + "," +
                replaceWhitespaceWithPlus(quarter) + "," +
                replaceWhitespaceWithPlus(city);
    }

    @VisibleForTesting
    public static String replaceWhitespaceWithPlus(String s) {
        String[] t = s.split(" ");
        StringBuilder sB = new StringBuilder();
        int tL = t.length;
        if (tL == 1) return t[0];
        else {
            for (int i = 0; i < tL; i++) {
                if ((i+1)==tL) sB.append(t[i]);
                else sB.append(t[i]).append("+");
            }
        }
        return sB.toString();
    }
}
