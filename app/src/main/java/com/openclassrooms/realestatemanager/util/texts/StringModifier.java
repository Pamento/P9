package com.openclassrooms.realestatemanager.util.texts;

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
}
