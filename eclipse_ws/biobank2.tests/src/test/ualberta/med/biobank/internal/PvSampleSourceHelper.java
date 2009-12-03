package test.ualberta.med.biobank.internal;

import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PvSampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleSourceWrapper;

public class PvSampleSourceHelper extends DbHelper {

    public static PvSampleSourceWrapper newPvSampleSource(String name,
        PatientVisitWrapper visit) throws Exception {
        PvSampleSourceWrapper pss = new PvSampleSourceWrapper(appService);
        SampleSourceWrapper ssw = SampleSourceHelper.addSampleSource(name);
        pss.setSampleSource(ssw);
        pss.setQuantity(r.nextInt(10));
        pss.setPatientVisit(visit);
        return pss;
    }

    public static PvSampleSourceWrapper addPvSampleSource(String name,
        PatientVisitWrapper visit) throws Exception {
        PvSampleSourceWrapper sss = newPvSampleSource(name, visit);
        sss.persist();
        return sss;
    }

    public static void addPvSampleSources(String name,
        PatientVisitWrapper visit, int count) throws Exception {
        for (int i = 0; i < count; i++) {
            addPvSampleSource(name + i, visit);
        }
    }

}
