package edu.ualberta.med.biobank.test.internal;

import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PvSourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleSourceWrapper;

public class PvSampleSourceHelper extends DbHelper {

    public static PvSourceVesselWrapper newPvSampleSource(String name,
        PatientVisitWrapper visit) throws Exception {
        PvSourceVesselWrapper pss = new PvSourceVesselWrapper(appService);
        SampleSourceWrapper ssw = SampleSourceHelper.addSampleSource(name);
        pss.setSourceVessel(ssw);
        pss.setQuantity(r.nextInt(10));
        pss.setPatientVisit(visit);
        return pss;
    }

    public static PvSourceVesselWrapper addPvSampleSource(String name,
        PatientVisitWrapper visit) throws Exception {
        PvSourceVesselWrapper sss = newPvSampleSource(name, visit);
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
