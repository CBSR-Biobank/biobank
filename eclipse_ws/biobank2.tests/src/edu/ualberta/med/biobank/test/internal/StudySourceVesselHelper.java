package edu.ualberta.med.biobank.test.internal;

import edu.ualberta.med.biobank.common.wrappers.SourceSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceVesselTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

@SuppressWarnings({ "unused", "deprecation" })
@Deprecated
public class StudySourceVesselHelper extends DbHelper {

    public static SourceSpecimenWrapper newStudySourceVessel(
        StudyWrapper study, SourceVesselTypeWrapper svType,
        boolean needTimeDrawn, boolean needOriginalVolume) {
        return null;
    }

    public static SourceSpecimenWrapper addStudySourceVessel(
        StudyWrapper study, SourceVesselTypeWrapper svType,
        boolean needTimeDrawn, boolean needOriginalVolume) throws Exception {
        return null;
    }

    public static int addStudySourceVessels(StudyWrapper study, String name,
        boolean needTimeDrawn, boolean needOriginalVolume) throws Exception {
        return -1;
    }
}
