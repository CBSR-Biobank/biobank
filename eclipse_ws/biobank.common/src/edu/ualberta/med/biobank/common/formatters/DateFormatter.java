package edu.ualberta.med.biobank.common.formatters;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateFormatter {

    public static final TimeZone LOCAL = TimeZone.getDefault();

    public static final TimeZone GMT = TimeZone.getTimeZone("GMT"); //$NON-NLS-1$

    public static final String DATE_FORMAT = "yyyy-MM-dd"; //$NON-NLS-1$

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm"; //$NON-NLS-1$

    public static final String TIME_FORMAT = "HH:mm"; //$NON-NLS-1$

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

    public static Date convertDate(TimeZone oldTimeZone, TimeZone newTimeZone,
        Date date) {
        if (date == null)
            return null;

        // TODO: too slow?
        SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_TIME_FORMAT);
        sdf1.setTimeZone(oldTimeZone);

        SimpleDateFormat sdf2 = new SimpleDateFormat(DATE_TIME_FORMAT);
        sdf2.setTimeZone(newTimeZone);
        String oldDate = sdf2.format(date);

        try {
            return sdf1.parse(oldDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

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

    public static Date dateNoSeconds(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static boolean compareDatesToMinutes(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        cal1.setTime(date1);
        cal2.setTime(date2);

        return cal1.get(Calendar.YEAR) == cal1.get(Calendar.YEAR)
            && cal1.get(Calendar.DAY_OF_YEAR) == cal1.get(Calendar.DAY_OF_YEAR)
            && cal1.get(Calendar.HOUR_OF_DAY) == cal1.get(Calendar.HOUR_OF_DAY)
            && cal1.get(Calendar.MINUTE) == cal1.get(Calendar.MINUTE);
    }

}
