package edu.ualberta.med.biobank.common.formatters;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";

    public static final String TIME_FORMAT = "HH:mm";

    public static final SimpleDateFormat dateFormatter = new SimpleDateFormat(
        DATE_FORMAT);

    public static final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(
        DATE_TIME_FORMAT);

    public static final SimpleDateFormat timeFormatter = new SimpleDateFormat(
        TIME_FORMAT);

    public static String formatAsDate(Date date) {
        return format(dateFormatter, date);
    }

    public static String formatAsDateTime(Date date) {
        return format(dateTimeFormatter, date);
    }

    public static String formatAsDateTime(Timestamp timestamp) {
        return format(dateTimeFormatter, timestamp);
    }

    public static String formatAsTime(Date date) {
        return format(timeFormatter, date);
    }

    public static String format(SimpleDateFormat sdf, Date date) {
        if (date == null) {
            return null;
        }
        return sdf.format(date);
    }

    public static String format(SimpleDateFormat sdf, Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return sdf.format(timestamp);
    }

    public static Date parseToDate(String string) {
        return parse(dateFormatter, string);
    }

    public static Date parseToDateTime(String string) {
        return parse(dateTimeFormatter, string);
    }

    public static Date parseToTime(String string) {
        return parse(timeFormatter, string);
    }

    public static Date parse(SimpleDateFormat sdf, String string) {
        if (string == null) {
            return null;
        }
        try {
            return sdf.parse(string);
        } catch (ParseException e) {
            return null;
        }
    }
}
