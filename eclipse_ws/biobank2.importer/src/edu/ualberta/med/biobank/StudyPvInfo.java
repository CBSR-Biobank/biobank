
package edu.ualberta.med.biobank;

import java.util.HashSet;

import edu.ualberta.med.biobank.model.PvInfo;
import edu.ualberta.med.biobank.model.PvInfoPossible;
import edu.ualberta.med.biobank.model.Study;

public class StudyPvInfo {

    public static PvInfo getPvInfoFromPossible(String label) throws Exception {
        PvInfoPossible pvInfoPossible = BioBank2Db.getInstance().getPvInfoPossible(
            label);

        PvInfo pvInfo = new PvInfo();
        pvInfo.setLabel(pvInfoPossible.getLabel());
        pvInfo.setPvInfoPossible(pvInfoPossible);
        pvInfo.setPvInfoType(pvInfoPossible.getPvInfoType());
        return pvInfo;
    }

    public static HashSet<PvInfo> assignCommonPvInfo(Study study)
        throws Exception {
        String [] labels = {
            "Date Received", "Date Processed", "Worksheet", "Aliquot Volume",
            "Blood Received", "PBMC Count", "Comments", };
        HashSet<PvInfo> pvInfoSet = new HashSet<PvInfo>();

        for (String label : labels) {
            PvInfo pvInfo = getPvInfoFromPossible(label);

            if (label.equals("Blood Received")) {
                pvInfo.setPossibleValues("");
            }

            pvInfoSet.add((PvInfo) BioBank2Db.getInstance().setObject(pvInfo));
        }
        return pvInfoSet;
    }

    public static Study assignStudyPvInfo(Study study, HashSet<PvInfo> pvInfoSet)
        throws Exception {
        study.setPvInfoCollection(pvInfoSet);
        return (Study) BioBank2Db.getInstance().setObject(study);
    }

    public static Study assignBbpPvInfo(Study study) throws Exception {
        HashSet<PvInfo> pvInfoSet = assignCommonPvInfo(study);
        PvInfo pvInfo = getPvInfoFromPossible("Consent");
        pvInfo.setPossibleValues("Surveillance;Genetic predisposition;Previous samples;Genetic mutation");
        pvInfoSet.add((PvInfo) BioBank2Db.getInstance().setObject(pvInfo));
        return assignStudyPvInfo(study, pvInfoSet);
    }

    public static Study assignKdcsInfo(Study study) throws Exception {
        HashSet<PvInfo> pvInfoSet = assignCommonPvInfo(study);
        return assignStudyPvInfo(study, pvInfoSet);
    }

    public static Study assignVasInfo(Study study) throws Exception {
        HashSet<PvInfo> pvInfoSet = assignCommonPvInfo(study);
        return assignStudyPvInfo(study, pvInfoSet);
    }

    public static Study assignRvsInfo(Study study) throws Exception {
        HashSet<PvInfo> pvInfoSet = assignCommonPvInfo(study);
        return assignStudyPvInfo(study, pvInfoSet);
    }

    public static Study assignNhsInfo(Study study) throws Exception {
        HashSet<PvInfo> pvInfoSet = assignCommonPvInfo(study);
        PvInfo pvInfo = getPvInfoFromPossible("Visit Type");
        pvInfo.setPossibleValues("D0;D2;D4;W2;W4;M2;M6;M12;M18;M24;Unscheduled");
        pvInfo = (PvInfo) BioBank2Db.getInstance().setObject(pvInfo);
        pvInfoSet.add((PvInfo) BioBank2Db.getInstance().setObject(pvInfo));
        return assignStudyPvInfo(study, pvInfoSet);
    }

    public static Study assignMpsInfo(Study study) throws Exception {
        HashSet<PvInfo> pvInfoSet = assignCommonPvInfo(study);
        PvInfo pvInfo = getPvInfoFromPossible("Clinic Shipped Date");
        pvInfo = (PvInfo) BioBank2Db.getInstance().setObject(pvInfo);
        pvInfoSet.add((PvInfo) BioBank2Db.getInstance().setObject(pvInfo));
        return assignStudyPvInfo(study, pvInfoSet);
    }
}
