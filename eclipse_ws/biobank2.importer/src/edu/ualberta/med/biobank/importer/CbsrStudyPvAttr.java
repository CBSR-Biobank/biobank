
package edu.ualberta.med.biobank.importer;

import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.SitePvAttr;
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

    public static void assignAhfemPvAttr(StudyWrapper study) throws Exception {
        study.setStudyPvAttr("Worksheet", "text");
    }

    public static void assignBbpspPvAttr(StudyWrapper study) throws Exception {
        study.setStudyPvAttr("PBMC Count", "number");
        study.setStudyPvAttr("Consent", "select_multiple", new String [] {
            "Surveillance", "Genetic Predisposition", "Previous Samples",
            "Genetic Mutation" });
        study.setStudyPvAttr("Worksheet", "text");
    }

    public static void assignCegiirPvAttr(StudyWrapper study) throws Exception {
        study.setStudyPvAttr("PBMC Count", "number");
        study.setStudyPvAttr("Worksheet", "text");
    }

    public static void assignErcinPvAttr(StudyWrapper study) throws Exception {
        study.setStudyPvAttr("Visit Type", "select_single", new String [] {
            "Baseline", "Precath", "6hr Post", "24hr Post", "48-72hr Post" });
        study.setStudyPvAttr("Worksheet", "text");
    }

    public static void assignKdcsAttr(StudyWrapper study) throws Exception {
        study.setStudyPvAttr("PBMC Count", "number");
        study.setStudyPvAttr("Consent", "select_multiple",
            new String [] { "Genetic" });
        study.setStudyPvAttr("Worksheet", "text");
    }

    public static void assignKmsPvAttr(StudyWrapper study) throws Exception {
        study.setStudyPvAttr("Worksheet", "text");
    }

    public static void assignMpsPvAttr(StudyWrapper study) throws Exception {
        study.setStudyPvAttr("Worksheet", "text");
    }

    public static void assignNhsPvAttr(StudyWrapper study) throws Exception {
        study.setStudyPvAttr("Biopsy Length", "number");
        study.setStudyPvAttr("Visit Type", "select_single", new String [] {
            "D0", "D2", "D4", "Wk2", "Wk4", "M2", "M8", "M12", "M18", "M24" });
        study.setStudyPvAttr("PBMC Count", "number");
        study.setStudyPvAttr("Worksheet", "text");
    }

    public static void assignRvsPvAttr(StudyWrapper study) throws Exception {
        study.setStudyPvAttr("Worksheet", "text");
    }

    public static void assignTcksPvAttr(StudyWrapper study) throws Exception {
        study.setStudyPvAttr("PBMC Count", "number");
        study.setStudyPvAttr("Worksheet", "text");
    }

    public static void assignVasPvAttr(StudyWrapper study) throws Exception {
        study.setStudyPvAttr("PBMC Count", "number");
        study.setStudyPvAttr("Worksheet", "text");
    }
}
