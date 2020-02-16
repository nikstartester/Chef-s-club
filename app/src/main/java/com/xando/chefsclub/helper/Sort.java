package com.xando.chefsclub.helper;

import java.util.Comparator;

class Sort {

    public static Comparator<String> ALPHABETICAL_ORDER = (str1, str2) -> {
        int res = String.CASE_INSENSITIVE_ORDER.compare(str1, str2);
        if (res == 0) {
            res = str1.compareTo(str2);
        }
        return res;
    };
}
