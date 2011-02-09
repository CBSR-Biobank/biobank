package edu.ualberta.med.biobank.test.internal;

import edu.ualberta.med.biobank.common.wrappers.SourceVesselTypeWrapper;

public class SourceVesselTypeHelper extends DbHelper {

    public static SourceVesselTypeWrapper newSourceVesselType(String name) {
        SourceVesselTypeWrapper svt = new SourceVesselTypeWrapper(appService);
        svt.setName(name);
        return svt;
    }

}
