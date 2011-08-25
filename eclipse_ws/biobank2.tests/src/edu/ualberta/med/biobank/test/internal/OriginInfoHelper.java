package edu.ualberta.med.biobank.test.internal;

import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;

public class OriginInfoHelper extends DbHelper {

    public static OriginInfoWrapper newOriginInfo(CenterWrapper<?> center)
        throws Exception {
        OriginInfoWrapper oi = new OriginInfoWrapper(appService);
        oi.setCenter(center);
        return oi;
    }

    public static OriginInfoWrapper addOriginInfo(CenterWrapper<?> center)
        throws Exception {
        OriginInfoWrapper oi = newOriginInfo(center);
        oi.persist();
        return oi;
    }
}
