package edu.ualberta.med.biobank.test.internal;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.SourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class SourceVesselHelper extends DbHelper {

    public static List<SourceVesselWrapper> createdSourceVessels = new ArrayList<SourceVesselWrapper>();

    public static SourceVesselWrapper newSourceVessel(String name) {
        SourceVesselWrapper source = new SourceVesselWrapper(appService);
        source.setName(name);
        return source;
    }

    public static SourceVesselWrapper addSourceVessel(String name,
        boolean addToCreatedList) throws Exception {
        SourceVesselWrapper source = newSourceVessel(name);
        source.persist();
        if (addToCreatedList) {
            createdSourceVessels.add(source);
        }
        return source;
    }

    public static SourceVesselWrapper addSourceVessel(String name)
        throws Exception {
        return addSourceVessel(name, true);
    }

    public static void deleteCreatedSourceVessels() throws Exception {
        for (SourceVesselWrapper source : createdSourceVessels) {
            source.reload();
            source.delete();
        }
        createdSourceVessels.clear();
    }

    public static int addSourceVessels(StudyWrapper study, String name)
        throws Exception {
        int nber = r.nextInt(15) + 1;
        List<SourceVesselWrapper> sources = new ArrayList<SourceVesselWrapper>();
        for (int i = 0; i < nber; i++) {
            sources.add(SourceVesselHelper.addSourceVessel(name + i));
        }
        study.addSourceVessels(sources);
        study.persist();
        return nber;
    }

}
