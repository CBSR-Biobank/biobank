package edu.ualberta.med.biobank.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import edu.ualberta.med.biobank.model.Comment;

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
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2000 + r.nextInt(40));
        cal.set(Calendar.DAY_OF_YEAR, r.nextInt(365) + 1);
        cal.set(Calendar.HOUR_OF_DAY, r.nextInt(24));
        cal.set(Calendar.MINUTE, r.nextInt(60));
        cal.set(Calendar.SECOND, r.nextInt(60));
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static List<Comment> getRandomComments() {
        final List<Comment> comments = new ArrayList<Comment>();
    
        for (int i = 0, n = r.nextInt(5); i < n; ++i) {
            Comment c = new Comment();
            c.setDateAdded(getRandomDate());
            c.setText("comment " + r.nextInt());
            comments.add(c);
        }
        return comments;
    }

}
