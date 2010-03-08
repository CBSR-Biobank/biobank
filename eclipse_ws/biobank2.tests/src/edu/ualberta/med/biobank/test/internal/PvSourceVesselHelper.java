package edu.ualberta.med.biobank.test.internal;

import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PvSourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceVesselWrapper;

public class PvSourceVesselHelper extends DbHelper {

    public static PvSourceVesselWrapper newPvSourceVessel(String name,
        PatientVisitWrapper visit) throws Exception {
        PvSourceVesselWrapper pss = new PvSourceVesselWrapper(appService);
        SourceVesselWrapper ssw = SourceVesselHelper.addSourceVessel(name);
        pss.setSourceVessel(ssw);
        pss.setQuantity(r.nextInt(10));
        pss.setPatientVisit(visit);
        return pss;
    }

    public static PvSourceVesselWrapper addPvSourceVessel(String name,
        PatientVisitWrapper visit) throws Exception {
        PvSourceVesselWrapper sss = newPvSourceVessel(name, visit);
        sss.persist();
        return sss;
    }

    public static void addPvSourceVessels(String name,
        PatientVisitWrapper visit, int count) throws Exception {
        for (int i = 0; i < count; i++) {
            addPvSourceVessel(name + i, visit);
        }
    }

}
