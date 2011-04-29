package edu.ualberta.med.biobank.test.internal;

import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;

public class CenterHelper extends DbHelper {

    public static void deleteCenterDependencies(CenterWrapper<?> center)
        throws Exception {
        center.reload();

        for (SpecimenWrapper spc : center.getSpecimenCollection(false)) {
            if (spc.getOriginInfo().getCenter().equals(center)) {
                spc.delete();
            }
        }
        deleteFromList(center.getOriginInfoCollection(false));
    }

}
