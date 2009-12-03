
package edu.ualberta.med.biobank.importer;

import java.util.HashSet;

import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.SitePvAttr;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyPvAttr;

public class CbsrStudyPvAttr {

    public static StudyPvAttr getSitePvAttr2SitePvAttr(Site site, String label)
        throws Exception {
        SitePvAttr sitePvAttr = BioBank2Db.getInstance().getSitePvAttr(site,
            label);

        StudyPvAttr studyPvAttr = new StudyPvAttr();
        studyPvAttr.setLabel(sitePvAttr.getLabel());
        return studyPvAttr;
    }

    public static HashSet<StudyPvAttr> assignCommonPvInfo(Study study)
        throws Exception {
        String [] labels = { "Worksheet", "PBMC Count" };
        HashSet<StudyPvAttr> pvInfoSet = new HashSet<StudyPvAttr>();

        for (String label : labels) {
            StudyPvAttr pvInfo = getSitePvAttr2SitePvAttr(study.getSite(),
                label);

            if (label.equals("Blood Received")) {
                pvInfo.setPermissible("");
            }

            pvInfoSet.add((StudyPvAttr) BioBank2Db.getInstance().setObject(
                pvInfo));
        }
        return pvInfoSet;
    }

    public static Study assignStudyPvAttr(Study study,
        HashSet<StudyPvAttr> pvInfoSet) throws Exception {
        study.setStudyPvAttrCollection(pvInfoSet);
        return (Study) BioBank2Db.getInstance().setObject(study);
    }

    public static Study assignBbpPvInfo(Study study) throws Exception {
        HashSet<StudyPvAttr> pvInfoSet = assignCommonPvInfo(study);
        StudyPvAttr pvInfo = getSitePvAttr2SitePvAttr(study.getSite(),
            "Consent");
        pvInfo.setPermissible("Surveillance;Genetic predisposition;Previous samples;Genetic mutation");
        pvInfoSet.add((StudyPvAttr) BioBank2Db.getInstance().setObject(pvInfo));
        return assignStudyPvAttr(study, pvInfoSet);
    }

    public static Study assignKdcsInfo(Study study) throws Exception {
        HashSet<StudyPvAttr> pvInfoSet = assignCommonPvInfo(study);
        return assignStudyPvAttr(study, pvInfoSet);
    }

    public static Study assignVasInfo(Study study) throws Exception {
        HashSet<StudyPvAttr> pvInfoSet = assignCommonPvInfo(study);
        return assignStudyPvAttr(study, pvInfoSet);
    }

    public static Study assignRvsInfo(Study study) throws Exception {
        HashSet<StudyPvAttr> pvInfoSet = assignCommonPvInfo(study);
        return assignStudyPvAttr(study, pvInfoSet);
    }

    public static Study assignNhsInfo(Study study) throws Exception {
        HashSet<StudyPvAttr> pvInfoSet = assignCommonPvInfo(study);
        StudyPvAttr pvInfo = getSitePvAttr2SitePvAttr(study.getSite(),
            "Visit Type");
        pvInfo.setPermissible("D0;D2;D4;W2;W4;M2;M6;M12;M18;M24;Unscheduled");
        pvInfo = (StudyPvAttr) BioBank2Db.getInstance().setObject(pvInfo);
        pvInfoSet.add((StudyPvAttr) BioBank2Db.getInstance().setObject(pvInfo));
        return assignStudyPvAttr(study, pvInfoSet);
    }

    public static Study assignMpsInfo(Study study) throws Exception {
        HashSet<StudyPvAttr> pvInfoSet = assignCommonPvInfo(study);
        StudyPvAttr pvInfo = getSitePvAttr2SitePvAttr(study.getSite(),
            "Clinic Shipped Date");
        pvInfo = (StudyPvAttr) BioBank2Db.getInstance().setObject(pvInfo);
        pvInfoSet.add((StudyPvAttr) BioBank2Db.getInstance().setObject(pvInfo));
        return assignStudyPvAttr(study, pvInfoSet);
    }
}
