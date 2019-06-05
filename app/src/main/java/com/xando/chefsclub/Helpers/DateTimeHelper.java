package com.xando.chefsclub.Helpers;

import android.support.annotation.NonNull;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;


public class DateTimeHelper {

    public static String simpleTransform(long time) {
        DateTimeFormatter df;

        DateTime dateTime = new DateTime(time);

        //show with day.month.year(like Dec 28, 2017)

        df = DateTimeFormat.forPattern("MMM dd, yyyy").withLocale(Locale.UK);

        return dateTime.toString(df);
    }

    public static String transform(long time) {
        DateTimeFormatter df;

        DateTime dateTime = new DateTime(time);

        DateTime currLocalDateTime = new DateTime(System.currentTimeMillis());

        if ((Math.abs(dateTime.getYear() - currLocalDateTime.getYear()) > 0)) {
            //show with day.month.year(like Dec 28, 2017)

            df = DateTimeFormat.forPattern("MMM dd, yyyy").withLocale(Locale.UK);
        } else if (Math.abs(dateTime.getMonthOfYear() - currLocalDateTime.getMonthOfYear()) > 0) {
            //show with day.month (like Dec 28)

            df = DateTimeFormat.forPattern("MMM dd").withLocale(Locale.UK);
        } else if (Math.abs(dateTime.getDayOfMonth() - currLocalDateTime.getDayOfMonth()) > 6) {
            //show with day.month (like Dec 28)

            df = DateTimeFormat.forPattern("MMM dd").withLocale(Locale.UK);
        } else if (Math.abs(dateTime.getDayOfMonth() - currLocalDateTime.getDayOfMonth()) > 0) {
            //show with day(like Friday)

            df = DateTimeFormat.forPattern("EEE").withLocale(Locale.UK);
        } else {
            //show time(like 15:10)

            df = DateTimeFormat.forPattern("HH:mm");
        }

        return dateTime.toString(df);
    }

    @NonNull
    public static String convertTime(int intTime) {
        int hours = intTime / 60;

        int minutes = intTime % 60;

        String strTime = "";
        if (hours > 0) {
            strTime = hours + "h ";
        }
        if (minutes > 0) {
            strTime += minutes + "m";
        }
        return strTime;
    }
}
