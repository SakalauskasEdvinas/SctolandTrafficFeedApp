package com.gcu.mobilecoursework.helper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
// By Edvinas Sakalauskas - S1627176

public class DateUtility {

    public static String dateToString(Date date) {
        DateFormat format = new SimpleDateFormat("dd-MM-y", Locale.UK);
        return format.format(date);
    }

    public static Date stringToDate(String dateString) {
        DateFormat format = new SimpleDateFormat("EE, dd MMMMM y hh:mm:ss z", Locale.UK);
        DateFormat format2 = new SimpleDateFormat("EEEE, dd MMMMM y - kk:mm", Locale.UK);
        List<DateFormat> formatStrings = new ArrayList<>();
        formatStrings.add(format);
        formatStrings.add(format2);

        for (DateFormat formatString : formatStrings) {
            try {
                return formatString.parse(dateString.trim());
            } catch (ParseException e) {
            }
        }
        return null;
    }
}
