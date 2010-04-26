package edu.ualberta.med.biobank.test.internal;

import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PvSourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceVesselWrapper;

public class PvSourceVesselHelper extends DbHelper {

    public static PvSourceVesselWrapper newPvSourceVessel(
        SourceVesselWrapper sourceVessel, PatientVisitWrapper visit)
        throws Exception {
        PvSourceVesselWrapper pss = new PvSourceVesselWrapper(appService);
        pss.setSourceVessel(sourceVessel);
        pss.setQuantity(r.nextInt(10));
        pss.setPatientVisit(visit);
        return pss;
    }

    public static PvSourceVesselWrapper newPvSourceVessel(String name,
        PatientVisitWrapper visit) throws Exception {
        SourceVesselWrapper ssw = SourceVesselHelper.addSourceVessel(name);
        return newPvSourceVessel(ssw, visit);
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
