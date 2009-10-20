package test.ualberta.med.biobank;

import org.junit.Test;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.SampleSourceWrapper;
import edu.ualberta.med.biobank.model.SampleSource;

public class TestSampleSource extends TestDatabase {

    public static SampleSourceWrapper addSampleSource() throws BiobankCheckException, Exception {
        SampleSourceWrapper ssw = new SampleSourceWrapper(appService, new SampleSource());
        ssw.persist();
        return ssw;
    }

    @Test
    public void TestGetSetStudyCollection(boolean sort) {
      
    }

    @Test
    public void TestDeleteChecks() throws BiobankCheckException, Exception {
    }

    @Test
    public void persistChecks() throws BiobankCheckException, Exception {
    }

    @Test
    public void TestCompareTo() {
    }
}
