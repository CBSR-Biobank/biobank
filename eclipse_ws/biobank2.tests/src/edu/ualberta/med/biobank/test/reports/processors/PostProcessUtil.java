package edu.ualberta.med.biobank.test.reports.processors;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;
import edu.ualberta.med.biobank.common.util.AbstractRowPostProcess;

public class PostProcessUtil {
    protected static void checkExpected(AbstractRowPostProcess processor,
        List<Object[]> input, List<Object[]> expectedOutput) {

        int rowIndex = 0;
        for (Object[] before : input) {
            Object[] after = (Object[]) processor.rowPostProcess(before);

            System.out.println(Arrays.toString(before));
            System.out.println(Arrays.toString(after));

            Assert.assertTrue(Arrays.equals(after,
                expectedOutput.get(rowIndex++)));
        }
    }
}
