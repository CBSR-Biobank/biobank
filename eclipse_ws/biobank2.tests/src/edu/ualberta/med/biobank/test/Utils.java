package edu.ualberta.med.biobank.test;

import java.text.ParseException;
import java.util.Date;
import java.util.Random;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;

public class Utils {

    private static Random r = new Random();

    public static String getRandomString(int minlen, int maxlen) {
        String str = new String();
        for (int j = 0, n = minlen + r.nextInt(maxlen - minlen) + 1; j < n; ++j) {
            str += (char) ('A' + r.nextInt(26));
        }
        return str;
    }

    public static String getRandomString(int maxlen) {
        return getRandomString(0, maxlen);
    }

    public static String getRandomNumericString(int maxlen) {
        String str = new String();
        for (int j = 0, n = r.nextInt(maxlen) + 1; j < n; ++j) {
            str += (char) ('0' + r.nextInt(10));
        }
        return str;
    }

    public static Date getRandomDate() {
        try {
            String dateStr = String.format("%04d-%02d-%02d %02d:%02d", 2000 + r
                .nextInt(100), r.nextInt(12) + 1, r.nextInt(30) + 1, r
                .nextInt(24) + 1, r.nextInt(60) + 1);
            return DateFormatter.dateFormatter.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException("could not generate a random date");
        }
    }

}
