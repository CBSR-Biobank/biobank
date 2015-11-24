package edu.ualberta.med.biobank.common.util;

import java.util.Calendar;
import java.util.Date;

/**
 * This class should be used to compare dates since Hibernate can return java.sql.Timestamp for some
 * java.util.Date fields. Note that java.util.Timestamp.equals() will always return false if passed
 * a java.sql.Date, as the later does not have any nanonseconds.
 * 
 * <pre>
 * java.util.Date date = new java.util.Date();
 * java.util.Date stamp = new java.sql.Timestamp(date.getTime());
 * assertTrue(date.equals(stamp));
 * assertTrue(date.compareTo(stamp) == 0);
 * assertTrue(stamp.compareTo(date) == 0);
 * assertTrue(stamp.equals(date)); // &lt;-- FAILS
 * </pre>
 * 
 */
public class DateCompare {
    public static int compare(Date date1, Date date2, int[] fields) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        cal1.setTime(date1);
        cal2.setTime(date2);

        int diff = 0;

        for (int field : fields) {
            diff = cal2.get(field) - cal1.get(field);
            if (diff != 0)
                return diff;
        }

        return 0;
    }

    public static int compare(Date date1, Date date2) {
        return compare(date1, date2, new int[] { Calendar.YEAR, Calendar.DAY_OF_YEAR,
            Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND });
    }

    public static int compareWithoutSeconds(Date date1, Date date2) {
        return compare(date1, date2, new int[] { Calendar.YEAR, Calendar.DAY_OF_YEAR,
            Calendar.HOUR_OF_DAY, Calendar.MINUTE });
    }

}
