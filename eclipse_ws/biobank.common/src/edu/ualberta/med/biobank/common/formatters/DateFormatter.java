package edu.ualberta.med.biobank.common.formatters;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";

    public static final SimpleDateFormat dateFormatter = new SimpleDateFormat(
        DATE_FORMAT);

    public static final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(
        DATE_TIME_FORMAT);

    public static String formatAsDate(Date date) {
        return format(dateFormatter, date);
    }

    public static String formatAsDateTime(Date date) {
        return format(dateTimeFormatter, date);
    }

    public static String format(SimpleDateFormat sdf, Date date) {
        if (date == null) {
            return "";
        }
        return sdf.format(date);
    }
}
