package edu.ualberta.med.biobank.test.internal;

import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;

public class OriginInfoHelper extends DbHelper {

    public static OriginInfoWrapper addOriginInfo(CenterWrapper<?> center)
        throws Exception {
        OriginInfoWrapper oi = new OriginInfoWrapper(appService);
        oi.setCenter(center);
        oi.persist();
        return oi;
    }
}
