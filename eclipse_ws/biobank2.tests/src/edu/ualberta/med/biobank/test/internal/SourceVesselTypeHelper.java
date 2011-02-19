package edu.ualberta.med.biobank.test.internal;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.SourceVesselTypeWrapper;

public class SourceVesselTypeHelper extends DbHelper {

    private static final List<SourceVesselTypeWrapper> createdSourceVesselTypes = new ArrayList<SourceVesselTypeWrapper>();

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
            createdSourceVesselTypes.add(svt);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return svt;
    }

    public static void deletedCreatedSourceVesselTypes() throws Exception {
        for (SourceVesselTypeWrapper sst : createdSourceVesselTypes) {
            sst.reload();
            sst.delete();
        }
        createdSourceVesselTypes.clear();
    }

}
