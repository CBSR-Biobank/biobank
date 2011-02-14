package edu.ualberta.med.biobank.test.internal;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.SourceVesselTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudySourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class StudySourceVesselHelper extends DbHelper {

    public static StudySourceVesselWrapper newStudySourceVessel(
        StudyWrapper study, SourceVesselTypeWrapper sourceVessel) {
        StudySourceVesselWrapper source = new StudySourceVesselWrapper(
            appService);
        source.setStudy(study);
        source.setSourceVesselType(sourceVessel);
        return source;
    }

    public static StudySourceVesselWrapper addStudySourceVessel(
        StudyWrapper study, SourceVesselTypeWrapper sourceVesselT)
        throws Exception {
        StudySourceVesselWrapper source = newStudySourceVessel(study,
            sourceVesselT);
        source.persist();
        return source;
    }

    // public static void deleteCreatedSourceVessels() throws Exception {
    // for (SourceVesselWrapper source : createdSourceVessels) {
    // source.reload();
    // source.delete();
    // }
    // createdSourceVessels.clear();
    // }\

    public static int addStudySourceVessels(StudyWrapper study, String name)
        throws Exception {
        int nber = r.nextInt(15) + 1;
        List<StudySourceVesselWrapper> sources = new ArrayList<StudySourceVesselWrapper>();
        for (int i = 0; i < nber; i++) {
            SourceVesselTypeWrapper sourceVesselT = SourceVesselTypeHelper
                .newSourceVesselType("newST");
            sources.add(addStudySourceVessel(study, sourceVesselT));
        }
        study.addToStudySourceVesselCollection(sources);
        study.persist();
        return nber;
    }
}
