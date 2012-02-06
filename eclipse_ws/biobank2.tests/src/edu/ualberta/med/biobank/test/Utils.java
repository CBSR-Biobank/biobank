package edu.ualberta.med.biobank.test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Utils {

    private static Random R = new Random();

    public static String getRandomString(int minlen, int maxlen) {
        String str = new String();
        for (int j = 0, n = minlen + R.nextInt(maxlen - minlen) + 1; j < n; ++j) {
            str += (char) ('A' + R.nextInt(26));
        }
        return str;
    }

    public static String getRandomString(int maxlen) {
        return getRandomString(0, maxlen);
    }

    public static String getRandomNumericString(int maxlen) {
        String str = new String();
        for (int j = 0, n = R.nextInt(maxlen) + 1; j < n; ++j) {
            str += (char) ('0' + R.nextInt(10));
        }
        return str;
    }

    public static Date getRandomDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2000 + R.nextInt(40));
        cal.set(Calendar.DAY_OF_YEAR, R.nextInt(365) + 1);
        cal.set(Calendar.HOUR_OF_DAY, R.nextInt(24));
        cal.set(Calendar.MINUTE, R.nextInt(60));
        cal.set(Calendar.SECOND, R.nextInt(60));
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static <T> T listChooseOne(List<T> list) {
        if (list.size() == 1) {
            return list.get(0);
        }
        if (list.size() > 1) {
            return list.get(R.nextInt(list.size()));
        }
        return null;
    }

}
