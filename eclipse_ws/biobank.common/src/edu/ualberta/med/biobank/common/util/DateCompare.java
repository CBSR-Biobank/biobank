package edu.ualberta.med.biobank.common.util;

import java.util.Calendar;
import java.util.Date;

/**
 * This class should be used to compare dates since Hibernate can return
 * java.sql.Timestamp for some java.util.Date fields. Note that
 * java.util.Date.equals() will always return false if passed a
 * java.sqlTimestamp.
 * 
 */
public class DateCompare {

    public static int compare(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        cal1.setTime(date1);
        cal2.setTime(date2);

        int[] fields = new int[] { Calendar.YEAR, Calendar.DAY_OF_YEAR,
            Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND };

        int diff = 0;

        for (int field : fields) {
            diff = cal2.get(field) - cal1.get(field);
            if (diff != 0)
                return diff;
        }

        return 0;
    }

}
