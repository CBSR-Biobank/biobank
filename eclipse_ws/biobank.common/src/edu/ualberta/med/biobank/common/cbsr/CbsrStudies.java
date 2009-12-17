package edu.ualberta.med.biobank.common.cbsr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.common.wrappers.SampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class CbsrStudies {

    private static Map<String, StudyWrapper> studiesMap = null;

    private static Map<String, SampleSourceWrapper> sampleSourceMap = null;

    public static void createStudies(SiteWrapper site) throws Exception {
        studiesMap = new HashMap<String, StudyWrapper>();

        sampleSourceMap = new HashMap<String, SampleSourceWrapper>();
        for (SampleSourceWrapper sampleSource : SampleSourceWrapper
            .getAllSampleSources(site.getAppService())) {
            sampleSourceMap.put(sampleSource.getName(), sampleSource);
        }

        StudyWrapper study;

        // STUDY - AHFEM
        study = addStudy(site, "Acute Heart Failure-Emergency Management",
            "AHFEM", null);
        study.setStudyPvAttr("Worksheet", "text");

        // STUDY - BBPSP
        study = addStudy(site, "Blood Borne Pathogens Surveillance Project",
            "BBPSP", null);
        study.setStudyPvAttr("PBMC Count", "number");
        study.setStudyPvAttr("Consent", "select_multiple", new String[] {
            "Surveillance", "Genetic Predisposition", "Previous Samples",
            "Genetic Mutation" });
        study.setStudyPvAttr("Worksheet", "text");
        study.setContactCollection(Arrays.asList(CbsrClinics
            .getContact("Morna Brown"), CbsrClinics
            .getContact("Melanie Peters"),
            CbsrClinics.getContact("Niki Davis"), CbsrClinics
                .getContact("Aleasha Warner"), CbsrClinics
                .getContact("Theresa Almonte"), CbsrClinics
                .getContact("Maryanne Gibson"), CbsrClinics
                .getContact("Sheila Schembri"), CbsrClinics
                .getContact("Dorine Belliveau"), CbsrClinics
                .getContact("Elaine Gloutnez"), CbsrClinics
                .getContact("Nathalie Aubin"), CbsrClinics
                .getContact("Lucie Lacasse"), CbsrClinics
                .getContact("Tammy Burtenshaw"), CbsrClinics
                .getContact("Chantal Gagne"), CbsrClinics
                .getContact("Elizabeth-Ann Paradis"), CbsrClinics
                .getContact("Dianne Dufour"), CbsrClinics
                .getContact("Colleen Fitzgerald")));
        study.setSampleSourceCollection(Arrays.asList(sampleSourceMap
            .get("10mL lavender top EDTA tube")));
        study.persist();

        // STUDY - CEGIIR
        study = addStudy(
            site,
            "Centre of Excellence for Gastrointestinal Inflammation and Immunity Research",
            "CEGIIR", null);
        study.setStudyPvAttr("PBMC Count", "number");
        study.setStudyPvAttr("Worksheet", "text");
        study.persist();

        // STUDY - CHILD
        addStudy(site, "Canadian Health Infant Longitudinal Development Study",
            "CHILD", null);

        // STUDY - ERCIN
        study = addStudy(
            site,
            "Exploring the Renoprotective effects of fluid prophylaxis strategies for Contrast Induced Nephropathy (Study)",
            "ERCIN", "Precath visit - only urine is collected");
        study.setStudyPvAttr("Visit Type", "select_single", new String[] {
            "Baseline", "Precath", "6hr Post", "24hr Post", "48-72hr Post" });
        study.setStudyPvAttr("Worksheet", "text");
        study.setContactCollection(Arrays.asList(CbsrClinics
            .getContact("Candace Dando"), CbsrClinics
            .getContact("Daisy Gibbons")));
        study.persist();

        // STUDY - KDCS
        study = addStudy(site, "Kidney Disease Cohort Study", "KDCS", null);
        study.setStudyPvAttr("PBMC Count", "number");
        study.setStudyPvAttr("Consent", "select_multiple",
            new String[] { "Genetic" });
        study.setStudyPvAttr("Worksheet", "text");
        study.setContactCollection(Arrays.asList(CbsrClinics
            .getContact("Charlynn Ursu"), CbsrClinics
            .getContact("Coralea Bignell"), CbsrClinics
            .getContact("Sharon Gulewich"), CbsrClinics
            .getContact("Dawn Opgenorth"), CbsrClinics
            .getContact("Phillo King"), CbsrClinics
            .getContact("Sharon Mollins"), CbsrClinics
            .getContact("Janilee Dow"), CbsrClinics.getContact("Jodie Hingst"),
            CbsrClinics.getContact("Ann Chala"), CbsrClinics
                .getContact("Cathy Lent")));
        study.persist();

        // STUDY - KMS
        study = addStudy(site, "Kingston Merger Study", "KMS", null);
        study.setStudyPvAttr("Worksheet", "text");
        study.persist();

        // STUDY - LCS
        study = addStudy(site, "Laboratory Cotnrols Study", "LCS", null);
        study.setStudyPvAttr("Worksheet", "text");
        study.persist();

        // STUDY - MPS
        study = addStudy(site, "Man-Chui Poon Study", "MPS", null);
        study.setStudyPvAttr("Worksheet", "text");
        study.persist();

        // STUDY - NHS
        study = addStudy(site, "Novartis Hepatitis C Study", "NHS", null);
        study.setStudyPvAttr("Biopsy Length", "number");
        study.setStudyPvAttr("Visit Type", "select_single", new String[] {
            "D0", "D2", "D4", "Wk2", "Wk4", "M2", "M8", "M12", "M18", "M24" });
        study.setStudyPvAttr("PBMC Count", "number");
        study.setStudyPvAttr("Worksheet", "text");
        study.setContactCollection(Arrays.asList(CbsrClinics
            .getContact("Shirley Cole"), CbsrClinics
            .getContact("Dr. Andrew Mason")));
        study.persist();

        // STUDY - RVS
        study = addStudy(site, "Retroviral Study", "RVS", null);
        study.setStudyPvAttr("Worksheet", "text");
        study.setContactCollection(Arrays.asList(CbsrClinics
            .getContact("Dr. Andrew Mason")));
        study.persist();

        // STUDY - TCKS
        study = addStudy(site, "Tonelli Chronic Kidney Study", "TCKS", null);
        study.setStudyPvAttr("PBMC Count", "number");
        study.setStudyPvAttr("Worksheet", "text");
        study.setContactCollection(Arrays.asList(CbsrClinics
            .getContact("Charlynn Ursu"), CbsrClinics
            .getContact("Coralea Bignell"), CbsrClinics
            .getContact("Sharon Gulewich"), CbsrClinics
            .getContact("Dawn Opgenorth")));
        study.persist();

        // STUDY - VAS
        study = addStudy(site, "Vascular Access Study", "VAS", null);
        study.setStudyPvAttr("PBMC Count", "number");
        study.setStudyPvAttr("Worksheet", "text");
        study.setContactCollection(Arrays.asList(CbsrClinics
            .getContact("Charlynn Ursu"), CbsrClinics
            .getContact("Coralea Bignell"), CbsrClinics
            .getContact("Sharon Gulewich"), CbsrClinics
            .getContact("Dawn Opgenorth"), CbsrClinics
            .getContact("Phillo King"), CbsrClinics
            .getContact("Sharon Mollins"), CbsrClinics
            .getContact("Janilee Dow"), CbsrClinics.getContact("Ann Chala"),
            CbsrClinics.getContact("Cathy Lent")));
        study.persist();
    }

    private static StudyWrapper addStudy(SiteWrapper site, String name,
        String nameShort, String comment) throws Exception {
        StudyWrapper study = new StudyWrapper(site.getAppService());
        study.setSite(site);
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

    public static List<String> getStudyNames() throws Exception {
        if (studiesMap == null) {
            throw new Exception("contacts have not been added");
        }
        return new ArrayList<String>(studiesMap.keySet());
    }

}
