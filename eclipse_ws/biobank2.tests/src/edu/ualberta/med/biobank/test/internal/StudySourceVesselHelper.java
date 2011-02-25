package edu.ualberta.med.biobank.test.internal;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.SourceVesselTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.test.Utils;

public class StudySourceVesselHelper extends DbHelper {

    public static SourceSpecimenWrapper newStudySourceVessel(
        StudyWrapper study, SourceVesselTypeWrapper svType,
        boolean needTimeDrawn, boolean needOriginalVolume) {
        SourceSpecimenWrapper ssv = new SourceSpecimenWrapper(appService);
        ssv.setStudy(study);
        ssv.setSourceVesselType(svType);
        ssv.setNeedTimeDrawn(needTimeDrawn);
        ssv.setNeedOriginalVolume(needOriginalVolume);
        return ssv;
    }

    public static SourceSpecimenWrapper addStudySourceVessel(
        StudyWrapper study, SourceVesselTypeWrapper svType,
        boolean needTimeDrawn, boolean needOriginalVolume) throws Exception {
        SourceSpecimenWrapper ssv = newStudySourceVessel(study, svType,
            needTimeDrawn, needOriginalVolume);
        ssv.persist();
        return ssv;
    }

    public static int addStudySourceVessels(StudyWrapper study, String name,
        boolean needTimeDrawn, boolean needOriginalVolume) throws Exception {
        int nber = r.nextInt(15) + 1;
        List<SourceSpecimenWrapper> ssvs = new ArrayList<SourceSpecimenWrapper>();
        for (int i = 0; i < nber; i++) {
            SourceVesselTypeWrapper svType = SourceVesselTypeHelper
                .addSourceVesselType("newST" + Utils.getRandomString(11));
            ssvs.add(addStudySourceVessel(study, svType, needTimeDrawn,
                needOriginalVolume));
        }
        study.addToStudySourceVesselCollection(ssvs);
        study.persist();
        return nber;
    }
}
