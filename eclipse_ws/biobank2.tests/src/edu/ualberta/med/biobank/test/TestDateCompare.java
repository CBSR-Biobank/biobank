package edu.ualberta.med.biobank.test;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.util.DateCompare;

public class TestDateCompare {

    private Random r;

    @Before
    public void setUp() throws Exception {
        r = new Random();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testDateCompare() throws Exception {
        Calendar cal = Calendar.getInstance();

        long timeLong = r.nextLong();
        cal.setTimeInMillis(timeLong);
        Date date1 = cal.getTime();
        Date date2 = new Date(timeLong);

        Assert.assertEquals(0, DateCompare.compare(date1, date2));

        date2 = new Date(timeLong - 1000); // one second less
        Assert.assertTrue(DateCompare.compare(date1, date2) < 0);

        date2 = new Date(timeLong + 1000); // one second more
        Assert.assertTrue(DateCompare.compare(date1, date2) > 0);

    }
}
