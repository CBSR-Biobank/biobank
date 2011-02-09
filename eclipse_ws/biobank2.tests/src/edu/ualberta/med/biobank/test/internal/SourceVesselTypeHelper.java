package edu.ualberta.med.biobank.test.internal;

import edu.ualberta.med.biobank.common.wrappers.SourceVesselTypeWrapper;

public class SourceVesselTypeHelper extends DbHelper {

    public static SourceVesselTypeWrapper newSourceVesselType(String name) {
        SourceVesselTypeWrapper svt = new SourceVesselTypeWrapper(appService);
        svt.setName(name);
        return svt;
    }

    public static SourceVesselTypeWrapper addSourceVesselType(String name) {
        SourceVesselTypeWrapper svt = new SourceVesselTypeWrapper(appService);
        svt.setName(name);
        try {
            svt.persist();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return svt;
    }

}
