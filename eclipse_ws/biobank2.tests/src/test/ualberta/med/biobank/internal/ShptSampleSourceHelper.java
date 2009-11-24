package test.ualberta.med.biobank.internal;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShptSampleSourceWrapper;
import edu.ualberta.med.biobank.model.SampleSource;
import edu.ualberta.med.biobank.model.ShptSampleSource;

public class ShptSampleSourceHelper extends DbHelper {

    public static ShptSampleSourceWrapper addPvSampleSource(String name,
        PatientVisitWrapper pv) throws BiobankCheckException, Exception {
        ShptSampleSourceWrapper pss = new ShptSampleSourceWrapper(appService,
            new ShptSampleSource());
        // FIXME to finish
        SampleSourceWrapper ssw = new SampleSourceWrapper(appService,
            new SampleSource());
        ssw.setName(name);
        ssw.persist();
        pss.setSampleSource(ssw.getWrappedObject());
        pss.persist();
        return pss;
    }
}
