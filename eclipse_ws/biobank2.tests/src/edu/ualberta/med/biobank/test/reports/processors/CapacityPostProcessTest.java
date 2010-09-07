package edu.ualberta.med.biobank.test.reports.processors;

import java.util.ArrayList;

import org.junit.Test;

import edu.ualberta.med.biobank.common.util.CapacityPostProcess;

public class CapacityPostProcessTest {
    @SuppressWarnings("serial")
    @Test
    public void test() {
        TestPostProcessUtil.checkExpected(new CapacityPostProcess(1, 0),
            new ArrayList<Object[]>() {
                {
                    add(new Object[] { new Long(1), new Long(2) });
                    add(new Object[] { new Long(2), new Long(3) });
                    add(new Object[] { new Long(3), new Long(4) });
                }
            }, new ArrayList<Object[]>() {
                {
                    add(new Object[] { new Long(1), new Long(2), "50.00%" });
                    add(new Object[] { new Long(2), new Long(3), "66.67%" });
                    add(new Object[] { new Long(3), new Long(4), "75.00%" });
                }
            });
    }

    @SuppressWarnings("serial")
    @Test
    public void testSameColumn() {
        TestPostProcessUtil.checkExpected(new CapacityPostProcess(0, 0),
            new ArrayList<Object[]>() {
                {
                    add(new Object[] { new Long(1) });
                    add(new Object[] { new Long(2) });
                }
            }, new ArrayList<Object[]>() {
                {
                    add(new Object[] { new Long(1), "100.00%" });
                    add(new Object[] { new Long(2), "100.00%" });
                }
            });
    }
}
