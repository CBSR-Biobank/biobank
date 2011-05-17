package edu.ualberta.med.biobank.test.internal;

import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;

public class CenterHelper extends DbHelper {

    public static void deleteCenterDependencies(CenterWrapper<?> center)
        throws Exception {
        center.reload();

        // first delete aliquoted specimens
        for (SpecimenWrapper spc : center.getSpecimenCollection(false)) {
            if (spc.getOriginInfo().getCenter().equals(center)
                && (spc.getParentSpecimen() != null)) {
                OriginInfoWrapper oi = spc.getOriginInfo();
                oi.delete();
                spc.delete();
            }
        }

        // now delete source specimens
        center.reload();
        for (SpecimenWrapper spc : center.getSpecimenCollection(false)) {
            if (spc.getOriginInfo().getCenter().equals(center)) {
                OriginInfoWrapper oi = spc.getOriginInfo();
                ShipmentInfoWrapper shipInfo = oi.getShipmentInfo();
                if (shipInfo != null) {
                    shipInfo.delete();
                }
                oi.delete();
                spc.delete();
            }
        }
        deleteFromList(center.getOriginInfoCollection(false));
    }

}
