package com.fooberticus.tf2playercheck.utils;

import java.util.Comparator;

public class CurrencyStringComparator implements Comparator<String> {

    public static final CurrencyStringComparator INSTANCE = new CurrencyStringComparator();

    private CurrencyStringComparator(){}

    @Override
    public int compare(String s1, String s2) {
        if (s1.equals("--")) return -1;
        if (s2.equals("--")) return 1;
        Float f1 = Float.parseFloat(s1.replaceAll("[$,]", ""));
        Float f2 = Float.parseFloat(s2.replaceAll("[$,]", ""));
        return f1.compareTo(f2);
    }
}
