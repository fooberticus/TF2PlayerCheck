package com.fooberticus.tf2playercheck.utils;

import java.util.Comparator;

public class IntegerStringComparator implements Comparator<String> {

    public static final IntegerStringComparator INSTANCE = new IntegerStringComparator();

    private IntegerStringComparator(){}

    @Override
    public int compare(String s1, String s2) {
        if (s1.equals("--")) return -1;
        if (s2.equals("--")) return 1;
        Integer i1 = Integer.parseInt(s1);
        Integer i2 = Integer.parseInt(s2);
        return i1.compareTo(i2);
    }
}
