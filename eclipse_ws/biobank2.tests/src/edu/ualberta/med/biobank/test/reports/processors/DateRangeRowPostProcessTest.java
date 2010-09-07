package edu.ualberta.med.biobank.test.reports.processors;

import java.util.ArrayList;

import org.junit.Test;

import edu.ualberta.med.biobank.common.util.DateRangeRowPostProcess;

public class DateRangeRowPostProcessTest {
    @SuppressWarnings("serial")
    @Test
    public void testNotGroupByYear() {
        TestPostProcessUtil.checkExpected(
            new DateRangeRowPostProcess(false, 1), new ArrayList<Object[]>() {
                {
                    add(new Object[] { null, 1970, 0, null });
                    add(new Object[] { null, 1970, 1, null });
                }
            }, new ArrayList<Object[]>() {
                {
                    add(new Object[] { null, "0-1970", null });
                    add(new Object[] { null, "1-1970", null });
                }
            });
    }

    @SuppressWarnings("serial")
    @Test
    public void testGroupByYear() {
        TestPostProcessUtil.checkExpected(new DateRangeRowPostProcess(true, 1),
            new ArrayList<Object[]>() {
                {
                    add(new Object[] { null, 1970, 0, null });
                    add(new Object[] { null, 1970, 1, null });
                }
            }, new ArrayList<Object[]>() {
                {
                    add(new Object[] { null, 1970, null });
                    add(new Object[] { null, 1970, null });
                }
            });
    }
}
