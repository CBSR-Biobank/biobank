
package edu.ualberta.med.biobank.importer;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class CbsrStudyConfig {

    private static final Map<String, List<String>> studyPvAttrConfigMap;
    static {
        Map<String, List<String>> aMap = new HashMap<String, List<String>>();
        aMap.put("AHFEM", Arrays.asList("Worksheet", "text", null));
        studyPvAttrConfigMap = Collections.unmodifiableMap(aMap);
    };

    public static void assignAhfem(StudyWrapper study) throws Exception {
        study.setStudyPvAttr("Worksheet", "text");
    }

    public static void assignBbpsp(StudyWrapper study) throws Exception {
        study.setStudyPvAttr("PBMC Count", "number");
        study.setStudyPvAttr("Consent", "select_multiple", new String [] {
            "Surveillance", "Genetic Predisposition", "Previous Samples",
            "Genetic Mutation" });
        study.setStudyPvAttr("Worksheet", "text");
    }

    public static void assignCegiir(StudyWrapper study) throws Exception {
        study.setStudyPvAttr("PBMC Count", "number");
        study.setStudyPvAttr("Worksheet", "text");
    }

    public static void assignErcin(StudyWrapper study) throws Exception {
        study.setStudyPvAttr("Visit Type", "select_single", new String [] {
            "Baseline", "Precath", "6hr Post", "24hr Post", "48-72hr Post" });
        study.setStudyPvAttr("Worksheet", "text");
    }

    public static void assignKdcs(StudyWrapper study) throws Exception {
        study.setStudyPvAttr("PBMC Count", "number");
        study.setStudyPvAttr("Consent", "select_multiple",
            new String [] { "Genetic" });
        study.setStudyPvAttr("Worksheet", "text");
    }

    public static void assignKms(StudyWrapper study) throws Exception {
        study.setStudyPvAttr("Worksheet", "text");
    }

    public static void assignMps(StudyWrapper study) throws Exception {
        study.setStudyPvAttr("Worksheet", "text");
    }

    public static void assignNhs(StudyWrapper study) throws Exception {
        study.setStudyPvAttr("Biopsy Length", "number");
        study.setStudyPvAttr("Visit Type", "select_single", new String [] {
            "D0", "D2", "D4", "Wk2", "Wk4", "M2", "M8", "M12", "M18", "M24" });
        study.setStudyPvAttr("PBMC Count", "number");
        study.setStudyPvAttr("Worksheet", "text");
    }

    public static void assignRvs(StudyWrapper study) throws Exception {
        study.setStudyPvAttr("Worksheet", "text");
    }

    public static void assignTcks(StudyWrapper study) throws Exception {
        study.setStudyPvAttr("PBMC Count", "number");
        study.setStudyPvAttr("Worksheet", "text");
    }

    public static void assignVas(StudyWrapper study) throws Exception {
        study.setStudyPvAttr("PBMC Count", "number");
        study.setStudyPvAttr("Worksheet", "text");
    }
}
