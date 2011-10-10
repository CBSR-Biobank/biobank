package edu.ualberta.med.biobank.test.internal;

import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.peer.CenterPeer;
import edu.ualberta.med.biobank.common.peer.OriginInfoPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.model.Specimen;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class CenterHelper extends DbHelper {

    private static final String ALIQUOTED_SPEC_HQL = "select spec from "
        + Specimen.class.getName()
        + " as spec where spec."
        + Property.concatNames(SpecimenPeer.CURRENT_CENTER, CenterPeer.ID)
        + "=? and spec."
        + Property.concatNames(SpecimenPeer.ORIGIN_INFO, OriginInfoPeer.CENTER,
            CenterPeer.ID) + "=? and spec."
        + SpecimenPeer.PARENT_SPECIMEN.getName() + " is not null";

    private static final String PARENT_SPEC_HQL = "select spec from "
        + Specimen.class.getName()
        + " as spec where spec."
        + Property.concatNames(SpecimenPeer.CURRENT_CENTER, CenterPeer.ID)
        + "=? and spec."
        + Property.concatNames(SpecimenPeer.ORIGIN_INFO, OriginInfoPeer.CENTER,
            CenterPeer.ID) + "=? and spec."
        + SpecimenPeer.PARENT_SPECIMEN.getName() + " is null";

    public static void deleteCenterDependencies(CenterWrapper<?> center)
        throws Exception {
        center.reload();

        // first delete aliquoted specimens
        List<Specimen> specs = appService.query(new HQLCriteria(
            ALIQUOTED_SPEC_HQL, Arrays.asList(center.getId(), center.getId())));
        for (Specimen spec : specs) {
            new SpecimenWrapper(appService, spec).delete();
        }

        // now delete source specimens
        specs = appService.query(new HQLCriteria(PARENT_SPEC_HQL, Arrays
            .asList(center.getId(), center.getId())));
        for (Specimen spec : specs) {
            SpecimenWrapper specW = new SpecimenWrapper(appService, spec);
            OriginInfoWrapper oi = specW.getOriginInfo();
            ShipmentInfoWrapper shipInfo = oi.getShipmentInfo();
            if (shipInfo != null) {
                shipInfo.delete();
            }
            if (!specW.isNew())
                specW.delete();
        }
        center.reload();
        deleteFromList(center.getOriginInfoCollection(false));
    }
}
