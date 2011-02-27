package edu.ualberta.med.biobank.test.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceVesselTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceVesselWrapper;

@Deprecated
public class SourceVesselHelper extends DbHelper {

    public static List<SourceVesselWrapper> createdSourceVessels = new ArrayList<SourceVesselWrapper>();

    public static SourceVesselWrapper newSourceVessel(PatientWrapper patient,
        SourceVesselTypeWrapper svType, Date timeDrawn, Double volume) {
        return null;
    }

    public static SourceVesselWrapper newSourceVessel(PatientWrapper patient,
        Date timeDrawn, Double volume) {
        return null;
    }

    public static void deleteCreatedSourceVessels() throws Exception {
    }

}
