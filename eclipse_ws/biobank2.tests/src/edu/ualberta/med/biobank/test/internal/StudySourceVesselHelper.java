package edu.ualberta.med.biobank.test.internal;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.SourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudySourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.test.Utils;

public class StudySourceVesselHelper extends DbHelper {

    public static StudySourceVesselWrapper newStudySourceVessel(
        StudyWrapper study, SourceVesselWrapper sourceVessel) {
        StudySourceVesselWrapper source = new StudySourceVesselWrapper(
            appService);
        source.setStudy(study);
        source.setSourceVessel(sourceVessel);
        return source;
    }

    public static StudySourceVesselWrapper addStudySourceVessel(
        StudyWrapper study, SourceVesselWrapper sourceVessel) throws Exception {
        StudySourceVesselWrapper source = newStudySourceVessel(study,
            sourceVessel);
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
            SourceVesselWrapper sourceVessel = SourceVesselHelper
                .addSourceVessel(name + i, PatientHelper.newPatient("testP"),
                    Utils.getRandomDate(), 0.01);
            sources.add(addStudySourceVessel(study, sourceVessel));
        }
        study.addStudySourceVessels(sources);
        study.persist();
        return nber;
    }
}
