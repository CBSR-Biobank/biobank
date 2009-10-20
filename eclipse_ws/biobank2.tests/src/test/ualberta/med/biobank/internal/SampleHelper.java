package test.ualberta.med.biobank.internal;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.PvSampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.model.PvSampleSource;
import edu.ualberta.med.biobank.model.SampleSource;
import edu.ualberta.med.biobank.model.SampleType;

public class SampleHelper extends DbHelper {
    public static SampleSourceWrapper addSampleSource()
        throws BiobankCheckException, Exception {
        SampleSourceWrapper ssw = new SampleSourceWrapper(appService,
            new SampleSource());
        ssw.persist();
        return ssw;
    }

    public static PvSampleSourceWrapper newPvSampleSourceWrapper() {
        return new PvSampleSourceWrapper(appService, new PvSampleSource());
    }

    public static SampleTypeWrapper addSampleTypeWrapper()
        throws BiobankCheckException, Exception {
        SampleTypeWrapper stw = new SampleTypeWrapper(appService,
            new SampleType());
        stw.persist();
        return stw;
    }
}
