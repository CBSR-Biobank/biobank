
package edu.ualberta.med.biobank.importer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class SiteStudies {

    private static Map<String, StudyWrapper> studiesMap = null;

    public static void createStudies(SiteWrapper site) throws Exception {
        studiesMap = new HashMap<String, StudyWrapper>();

        addStudy(site, "Acute Heart Failure-Emergency Management", "AHFEM",
            null);
        studiesMap.get("AHFEM").setStudyPvAttr("Worksheet", "text");

        addStudy(site, "Blood Borne Pathogens Surveillance Project", "BBPSP",
            null);
        studiesMap.get("BBPSP").setStudyPvAttr("PBMC Count", "number");
        studiesMap.get("BBPSP").setStudyPvAttr(
            "Consent",
            "select_multiple",
            new String [] {
                "Surveillance", "Genetic Predisposition", "Previous Samples",
                "Genetic Mutation" });
        studiesMap.get("BBPSP").setStudyPvAttr("Worksheet", "text");
        studiesMap.get("BBPSP").setContactCollection(
            Arrays.asList(SiteClinics.getContact("Morna Brown")));

        addStudy(
            site,
            "Centre of Excellence for Gastrointestinal Inflammation and Immunity Research",
            "CEGIIR", null);
        studiesMap.get("CEGIIR").setStudyPvAttr("PBMC Count", "number");
        studiesMap.get("CEGIIR").setStudyPvAttr("Worksheet", "text");

        addStudy(site, "Canadian Health Infant Longitudinal Development Study",
            "CHILD", null);

        addStudy(
            site,
            "Exploring the Renoprotective effects of fluid prophylaxis strategies for Contrast Induced Nephropathy (Study)",
            "ERCIN", "Precath visit - only urine is collected");
        studiesMap.get("ERCIN").setStudyPvAttr(
            "Visit Type",
            "select_single",
            new String [] {
                "Baseline", "Precath", "6hr Post", "24hr Post", "48-72hr Post" });
        studiesMap.get("ERCIN").setStudyPvAttr("Worksheet", "text");

        addStudy(site, "Kidney Disease Cohort Study", "KDCS", null);
        studiesMap.get("KDCS").setStudyPvAttr("PBMC Count", "number");
        studiesMap.get("KDCS").setStudyPvAttr("Consent", "select_multiple",
            new String [] { "Genetic" });
        studiesMap.get("KDCS").setStudyPvAttr("Worksheet", "text");

        addStudy(site, "Kingston Merger Study", "KMS", null);
        studiesMap.get("KMS").setStudyPvAttr("Worksheet", "text");

        addStudy(site, "Man-Chui Poon Study", "MPS", null);
        studiesMap.get("MPS").setStudyPvAttr("Worksheet", "text");

        addStudy(site, "Novartis Hepatitis C Study", "NHS", null);
        studiesMap.get("NHS").setStudyPvAttr("Biopsy Length", "number");
        studiesMap.get("NHS").setStudyPvAttr(
            "Visit Type",
            "select_single",
            new String [] {
                "D0", "D2", "D4", "Wk2", "Wk4", "M2", "M8", "M12", "M18", "M24" });
        studiesMap.get("NHS").setStudyPvAttr("PBMC Count", "number");
        studiesMap.get("NHS").setStudyPvAttr("Worksheet", "text");

        addStudy(site, "Retroviral Study", "RVS", null);
        studiesMap.get("RVS").setStudyPvAttr("Worksheet", "text");

        addStudy(site, "Tonelli Chronic Kidney Study", "TCKS", null);
        studiesMap.get("TCKS").setStudyPvAttr("PBMC Count", "number");
        studiesMap.get("TCKS").setStudyPvAttr("Worksheet", "text");

        addStudy(site, "Vascular Access Study", "VAS", null);
        studiesMap.get("VAS").setStudyPvAttr("PBMC Count", "number");
        studiesMap.get("VAS").setStudyPvAttr("Worksheet", "text");
    }

    private static StudyWrapper addStudy(SiteWrapper site, String name,
        String nameShort, String comment) throws Exception {
        StudyWrapper study = new StudyWrapper(site.getAppService());
        study.setName(name);
        study.setNameShort(nameShort);
        study.setComment(comment);
        study.persist();
        study.reload();
        studiesMap.put(nameShort, study);
        return study;
    }

    public static StudyWrapper getStudy(String name) throws Exception {
        StudyWrapper study = studiesMap.get(name);
        if (study == null) {
            throw new Exception("study with name \"" + name
                + "\" does not exist");
        }
        return study;
    }

}
