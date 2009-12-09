
package edu.ualberta.med.biobank.importer;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class SiteStudies {

    private static final Map<String, List<String>> studyPvAttrConfigMap;
    static {
        Map<String, List<String>> aMap = new HashMap<String, List<String>>();
        aMap.put("AHFEM", Arrays.asList("Worksheet", "text", null));
        studyPvAttrConfigMap = Collections.unmodifiableMap(aMap);
    };

    public static void createAhfem(StudyWrapper study) throws Exception {
        study.setStudyPvAttr("Worksheet", "text");
    }

    public static void createBbpsp(StudyWrapper study) throws Exception {
        study.setStudyPvAttr("PBMC Count", "number");
        study.setStudyPvAttr("Consent", "select_multiple", new String [] {
            "Surveillance", "Genetic Predisposition", "Previous Samples",
            "Genetic Mutation" });
        study.setStudyPvAttr("Worksheet", "text");
    }

    public static void createCegiir(StudyWrapper study) throws Exception {
        study.setStudyPvAttr("PBMC Count", "number");
        study.setStudyPvAttr("Worksheet", "text");
    }

    public static void createErcin(StudyWrapper study) throws Exception {
        study.setStudyPvAttr("Visit Type", "select_single", new String [] {
            "Baseline", "Precath", "6hr Post", "24hr Post", "48-72hr Post" });
        study.setStudyPvAttr("Worksheet", "text");
    }

    public static void createKdcs(StudyWrapper study) throws Exception {
        study.setStudyPvAttr("PBMC Count", "number");
        study.setStudyPvAttr("Consent", "select_multiple",
            new String [] { "Genetic" });
        study.setStudyPvAttr("Worksheet", "text");
    }

    public static void createKms(StudyWrapper study) throws Exception {
        study.setStudyPvAttr("Worksheet", "text");
    }

    public static void createMps(StudyWrapper study) throws Exception {
        study.setStudyPvAttr("Worksheet", "text");
    }

    public static void createNhs(StudyWrapper study) throws Exception {
        study.setStudyPvAttr("Biopsy Length", "number");
        study.setStudyPvAttr("Visit Type", "select_single", new String [] {
            "D0", "D2", "D4", "Wk2", "Wk4", "M2", "M8", "M12", "M18", "M24" });
        study.setStudyPvAttr("PBMC Count", "number");
        study.setStudyPvAttr("Worksheet", "text");
    }

    public static void createRvs(StudyWrapper study) throws Exception {
        study.setStudyPvAttr("Worksheet", "text");
    }

    public static void createTcks(StudyWrapper study) throws Exception {
        study.setStudyPvAttr("PBMC Count", "number");
        study.setStudyPvAttr("Worksheet", "text");
    }

    public static void createVas(StudyWrapper study) throws Exception {
        study.setStudyPvAttr("PBMC Count", "number");
        study.setStudyPvAttr("Worksheet", "text");
    }
}
