package test.ualberta.med.biobank.internal;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PvSampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleSourceWrapper;
import edu.ualberta.med.biobank.model.PvSampleSource;
import edu.ualberta.med.biobank.model.SampleSource;

public class PvSampleSourceHelper extends DbHelper {

    public static PvSampleSourceWrapper addPvSampleSource(String name,
        PatientVisitWrapper pv) throws BiobankCheckException, Exception {
        PvSampleSourceWrapper pss = new PvSampleSourceWrapper(appService,
            new PvSampleSource());
        pss.setPatientVisit(pv);
        SampleSourceWrapper ssw = new SampleSourceWrapper(appService,
            new SampleSource());
        ssw.setName(name);
        ssw.persist();
        pss.setSampleSource(ssw.getWrappedObject());
        pss.persist();
        return pss;
    }
}
