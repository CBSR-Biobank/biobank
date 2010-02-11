package edu.ualberta.med.biobank.common.cbsr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleStorageWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

/**
 * Query to generate studies:
 * 
 * select name, name_short, comment from study
 * 
 * Query to generate sample sources:
 * 
 * select study.name_short, sample_source.name from study join
 * study_sample_source on study.id=study_sample_source.study_id join
 * sample_source on sample_source.id=study_sample_source.sample_source_id order
 * by study.name_short
 * 
 * Query to generate study pv attributes:
 * 
 * select study.name_short, study_pv_attr.label, pv_attr_type.name,
 * study_pv_attr.permissible from study join study_pv_attr on
 * study_pv_attr.study_id=study.id join pv_attr_type on
 * pv_attr_type.id=study_pv_attr.pv_attr_type_id order by study.name_short
 * 
 * Query to generate study contacts:
 * 
 * select contact.name, study.name_short from contact join clinic on
 * clinic.id=contact.clinic_id left join study_contact on contact.id=contact_id
 * join study on study.id=study_contact.study_id order by study.name_short
 */
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

        addStudy(site, "Acute Heart Failure-Emergency Management", "AHFEM",
            "Active", null);
        addStudy(site, "Blood Borne Pathogens Surveillance Project", "BBPSP",
            "Active", null);
        addStudy(
            site,
            "Centre of Excellence for Gastrointestinal Inflammation and Immunity Research",
            "CEGIIR", "Active", null);
        addStudy(site, "Canadian Health Infant Longitudinal Development Study",
            "CHILD", "Active", null);
        addStudy(site,
            "Exploring the Renoprotective effects of fluid prophylaxis "
                + "strategies for Contrast Induced Nephropathy (Study)",
            "ERCIN", "Disabled", "Precath visit - only urine is collected");
        addStudy(site, "Kidney Disease Cohort Study", "KDCS", "Active", null);
        addStudy(site, "Kingston Merger Study", "KMS", "Active", null);
        addStudy(site, "Man-Chui Poon Study", "MPS", "Active", null);
        addStudy(site, "Novartis Hepatitis C Study", "NHS", "Active", null);
        addStudy(site, "Retroviral Study", "RVS", "Active", null);
        addStudy(site, "Tonelli Chronic Kidney Study", "TCKS", "Active", null);
        addStudy(site, "Vascular Access Study", "VAS", "Active", null);
        addStudy(site, "Critical Care Cohort Study", "CCCS", "Closed", null);
        addStudy(
            site,
            "A phase II randomized blinded controlled trial of the effect of "
                + "furoSemide in cricially ill Patients with eARly acute Kidney injury",
            "SPARK", "Active", null);
        addStudy(
            site,
            "Laboratory Controls Study",
            "LCS",
            "Active",
            "Created so that we could give locations/track samples that we "
                + "use as controls. Also used to store leftover aliquots of staff "
                + "blood  used in experiments");

        addSampleSource("AHFEM", "5mL gold top serum tube");
        addSampleSource("AHFEM", "6ml light green top lithium heparin tube");
        addSampleSource("AHFEM", "EDTA cryovial");
        addSampleSource("AHFEM", "AHFEM processing pallet ");
        addSampleSource("BBPSP", "10mL lavender top EDTA tube");
        addSampleSource("BBPSP", "4ml lavender top EDTA tube");
        addSampleSource("BBPSP", "3mL lavender top EDTA tube");
        addSampleSource("BBPSP", "10ml orange top PAXgene tube");
        addSampleSource("BBPSP", "urine cup");
        addSampleSource("CCCS", "6mL lavender top EDTA tube");
        addSampleSource("CCCS", "6mL beige top tube");
        addSampleSource("CCCS", "urine cup");
        addSampleSource("CEGIIR", "10mL lavender top EDTA tube");
        addSampleSource("CEGIIR", "6mL beige top tube");
        addSampleSource("CEGIIR", "15ml centrifuge tube (sodium azide urine)");
        addSampleSource("CHILD", "6mL beige top tube");
        addSampleSource("CHILD", "10ml green top sodium heparin tube");
        addSampleSource("CHILD", "urine cup");
        addSampleSource("CHILD", "Nasal Swab");
        addSampleSource("CHILD", "Breast milk");
        addSampleSource("CHILD", "Meconium");
        addSampleSource("CHILD", "Stool");
        addSampleSource("ERCIN", "ERCIN Serum processing pallet");
        addSampleSource("ERCIN", "ERCIN Urine processing pallet");
        addSampleSource("KDCS", "10mL lavender top EDTA tube");
        addSampleSource("KDCS", "3mL lavender top EDTA tube");
        addSampleSource("KDCS", "6mL beige top tube");
        addSampleSource("KDCS", "3mL red top tube (hemodialysate)");
        addSampleSource("KDCS", "3ml red top tube (source water)");
        addSampleSource("KDCS", "10ml orange top PAXgene tube");
        addSampleSource("KDCS", "15ml centrifuge tube (sodium azide urine)");
        addSampleSource("KDCS", "6ml beige top tube (tap water)");
        addSampleSource("KDCS", "fingernail tube");
        addSampleSource("KDCS", "toenail tube");
        addSampleSource("KDCS", "hair bagette");
        addSampleSource("KMS", "EDTA cryovial");
        addSampleSource("LCS", "N/A");
        addSampleSource("LCS", "10mL lavender top EDTA tube");
        addSampleSource("LCS", "6mL lavender top EDTA tube");
        addSampleSource("LCS", "4ml lavender top EDTA tube");
        addSampleSource("LCS", "3mL lavender top EDTA tube");
        addSampleSource("LCS", "5mL gold top serum tube");
        addSampleSource("LCS", "6mL beige top tube");
        addSampleSource("LCS", "3mL red top tube (hemodialysate)");
        addSampleSource("LCS", "3ml red top tube (source water)");
        addSampleSource("LCS", "10ml green top sodium heparin tube");
        addSampleSource("LCS", "6ml light green top lithium heparin tube");
        addSampleSource("LCS", "10ml orange top PAXgene tube");
        addSampleSource("LCS", "15ml centrifuge tube (sodium azide urine)");
        addSampleSource("LCS", "6ml beige top tube (tap water)");
        addSampleSource("LCS", "urine cup");
        addSampleSource("LCS", "fingernail tube");
        addSampleSource("LCS", "toenail tube");
        addSampleSource("LCS", "hair bagette");
        addSampleSource("LCS", "4.5mL blue top Sodium citrate tube");
        addSampleSource("LCS", "2.7mL blue top Sodium citrate tube");
        addSampleSource("LCS", "15ml centrifuge tube (ascites fluid)");
        addSampleSource("LCS", "EDTA cryovial");
        addSampleSource("LCS", "Nasal Swab");
        addSampleSource("LCS", "Breast milk");
        addSampleSource("LCS", "Meconium");
        addSampleSource("LCS", "Stool");
        addSampleSource("LCS", "ERCIN Serum processing pallet");
        addSampleSource("LCS", "ERCIN Urine processing pallet");
        addSampleSource("LCS", "AHFEM processing pallet ");
        addSampleSource("MPS", "2.7mL blue top Sodium citrate tube");
        addSampleSource("NHS", "4ml lavender top EDTA tube");
        addSampleSource("NHS", "5mL gold top serum tube");
        addSampleSource("RVS", "10mL lavender top EDTA tube");
        addSampleSource("RVS", "5mL gold top serum tube");
        addSampleSource("RVS", "10ml orange top PAXgene tube");
        addSampleSource("RVS", "15ml centrifuge tube (sodium azide urine)");
        addSampleSource("SPARK", "4ml lavender top EDTA tube");
        addSampleSource("SPARK", "urine cup");
        addSampleSource("TCKS", "10mL lavender top EDTA tube");
        addSampleSource("TCKS", "6mL beige top tube");
        addSampleSource("TCKS", "10ml orange top PAXgene tube");
        addSampleSource("TCKS", "15ml centrifuge tube (sodium azide urine)");
        addSampleSource("TCKS", "fingernail tube");
        addSampleSource("TCKS", "toenail tube");
        addSampleSource("TCKS", "hair bagette");
        addSampleSource("VAS", "10mL lavender top EDTA tube");
        addSampleSource("VAS", "5mL gold top serum tube");
        addSampleSource("VAS", "6mL beige top tube");
        addSampleSource("VAS", "10ml orange top PAXgene tube");

        addSampleStorage("AHFEM", "Serum", 5, 0.2);
        addSampleStorage("AHFEM", "Plasma", 5, 0.2);
        addSampleStorage("AHFEM", "Lith Hep Plasma", 20, 0.2);
        addSampleStorage("AHFEM", "PF Plasma", 2, 0.2);
        addSampleStorage("BBPSP", "Paxgene", 11, 0.8);
        addSampleStorage("BBPSP", "Plasma", 16, 0.2);
        addSampleStorage("BBPSP", "DNA(Blood)", 2, 0.4);
        addSampleStorage("BBPSP", "CDPA Plasma", 3, 1.0);
        addSampleStorage("CEGIIR", "Z Urine", 6, 0.9);
        addSampleStorage("CEGIIR", "Cells", 5, 0.5);
        addSampleStorage("CEGIIR", "Serum B", 6, 0.8);
        addSampleStorage("CEGIIR", "Plasma", 5, 0.4);
        addSampleStorage("CEGIIR", "WB DMSO", 2, 0.8);
        addSampleStorage("CEGIIR", "DNA(Blood)", 2, 0.4);
        addSampleStorage("CHILD", "WB Serum", null, null);
        addSampleStorage("CHILD", "WB RNA", null, null);
        addSampleStorage("CHILD", "WB Plasma", null, null);
        addSampleStorage("CHILD", "WBlood", null, null);
        addSampleStorage("CHILD", "Serum Pel", null, null);
        addSampleStorage("CHILD", "CBMC RNA", null, null);
        addSampleStorage("CHILD", "Meconium", null, null);
        addSampleStorage("CHILD", "Plasma SH", null, null);
        addSampleStorage("CHILD", "CBMC", null, null);
        addSampleStorage("ERCIN", "Z Urine", 12, 0.9);
        addSampleStorage("ERCIN", "C Urine", 6, 0.9);
        addSampleStorage("ERCIN", "Serum B", 12, 0.4);
        addSampleStorage("KDCS", "S Water", 3, 0.9);
        addSampleStorage("KDCS", "Cells", 10, 0.4);
        addSampleStorage("KDCS", "Paxgene", 11, 0.8);
        addSampleStorage("KDCS", "T Nails", null, null);
        addSampleStorage("KDCS", "F Nails", null, null);
        addSampleStorage("KDCS", "Z Urine", 3, 0.4);
        addSampleStorage("KDCS", "Dialysate", 3, 0.4);
        addSampleStorage("KDCS", "Hair", null, null);
        addSampleStorage("KDCS", "Serum B", 8, 0.4);
        addSampleStorage("KDCS", "Serum", null, null);
        addSampleStorage("KDCS", "Plasma", 12, 0.4);
        addSampleStorage("KDCS", "WBE", 4, 0.8);
        addSampleStorage("KDCS", "DNA(Blood)", 2, 0.4);
        addSampleStorage("KDCS", "T Water", 3, 0.9);
        addSampleStorage("KMS", "Plasma", 20, 0.1);
        addSampleStorage("MPS", "Plasma", 3, 1.0);
        addSampleStorage("NHS", "Cells", 10, 0.5);
        addSampleStorage("NHS", "Serum", 16, 0.4);
        addSampleStorage("NHS", "Plasma", 12, 0.4);
        addSampleStorage("RVS", "Z Urine", 6, 0.9);
        addSampleStorage("RVS", "Cells", 10, 0.5);
        addSampleStorage("RVS", "Paxgene", 22, 0.8);
        addSampleStorage("RVS", "Serum", 4, 0.4);
        addSampleStorage("RVS", "Plasma", 16, 0.4);
        addSampleStorage("TCKS", "Paxgene", 11, 0.8);
        addSampleStorage("TCKS", "Cells", 10, 0.5);
        addSampleStorage("TCKS", "T Nails", null, null);
        addSampleStorage("TCKS", "F Nails", null, null);
        addSampleStorage("TCKS", "Hair", null, null);
        addSampleStorage("TCKS", "Z Urine", 3, 0.4);
        addSampleStorage("TCKS", "Serum B", 8, 0.4);
        addSampleStorage("TCKS", "Plasma", 12, 0.4);
        addSampleStorage("TCKS", "WBE", 4, 0.8);
        addSampleStorage("TCKS", "DNA(Blood)", 2, 0.4);
        addSampleStorage("VAS", "Cells", 10, 0.5);
        addSampleStorage("VAS", "Paxgene", 11, 0.8);
        addSampleStorage("VAS", "Serum B", 8, 0.4);
        addSampleStorage("VAS", "Serum", null, null);
        addSampleStorage("VAS", "Plasma", 12, 0.4);
        addSampleStorage("VAS", "WBE", 4, 0.8);
        addSampleStorage("VAS", "DNA(Blood)", 2, 0.4);
        addSampleStorage("CCCS", "WBE", 6, 0.9);
        addSampleStorage("CCCS", "Z Urine", 6, 0.9);
        addSampleStorage("CCCS", "C Urine", 6, 0.9);
        addSampleStorage("CCCS", "Serum B", 6, 0.9);
        addSampleStorage("CCCS", "Plasma", 6, 0.4);
        addSampleStorage("SPARK", "Plasma", 5, 0.5);
        addSampleStorage("SPARK", "C Urine", 5, 0.5);
        addSampleStorage("SPARK", "WB DMSO", 1, 0.5);

        addPvAttr("AHFEM", "PBMC Count", "number");
        addPvAttr("AHFEM", "Worksheet", "text");
        addPvAttr("BBPSP", "Consent", "select_multiple",
            "Surveillance;Genetic Predisposition;Previous Samples;Genetic Mutation");
        addPvAttr("BBPSP", "Worksheet", "text");
        addPvAttr("BBPSP", "PBMC Count", "number");
        addPvAttr("CCCS", "PBMC Count", "number");
        addPvAttr("CCCS", "Worksheet", "text");
        addPvAttr("CEGIIR", "Worksheet", "text");
        addPvAttr("CEGIIR", "PBMC Count", "number");
        addPvAttr("CHILD", "PBMC Count", "number");
        addPvAttr("CHILD", "Worksheet", "text");
        addPvAttr("ERCIN", "Worksheet", "text");
        addPvAttr("ERCIN", "PBMC Count", "number");
        addPvAttr("ERCIN", "Visit Type", "select_single",
            "Baseline;Precath;6hr Post;24hr Post;48-72hr Post");
        addPvAttr("KDCS", "Worksheet", "text");
        addPvAttr("KDCS", "Consent", "select_multiple", "Genetic");
        addPvAttr("KDCS", "PBMC Count", "number");
        addPvAttr("KMS", "PBMC Count", "number");
        addPvAttr("KMS", "Worksheet", "text");
        addPvAttr("LCS", "PBMC Count", "number");
        addPvAttr("LCS", "Worksheet", "text");
        addPvAttr("MPS", "PBMC Count", "number");
        addPvAttr("MPS", "PBMC Count", "number");
        addPvAttr("MPS", "Worksheet", "text");
        addPvAttr("NHS", "PBMC Count", "number");
        addPvAttr("NHS", "Worksheet", "text");
        addPvAttr("NHS", "Visit Type", "select_single",
            "D0;D2;D4;Wk2;Wk4;M2;M8;M12;M18;M24");
        addPvAttr("NHS", "Biopsy Length", "number");
        addPvAttr("RVS", "PBMC Count", "number");
        addPvAttr("RVS", "Worksheet", "text");
        addPvAttr("SPARK", "PBMC Count", "number");
        addPvAttr("SPARK", "Worksheet", "text");
        addPvAttr("TCKS", "PBMC Count", "number");
        addPvAttr("TCKS", "Worksheet", "text");
        addPvAttr("VAS", "PBMC Count", "number");
        addPvAttr("VAS", "Worksheet", "text");

        addContact("Dr. Justin Ezekowitz", "ED1", "AHFEM");
        addContact("Morna Brown", "CL1", "BBPSP");
        addContact("Melanie Peters", "ED1", "BBPSP");
        addContact("Aleasha Warner", "HL2", "BBPSP");
        addContact("Dorine Belliveau", "MC1", "BBPSP");
        addContact("Elaine Gloutnez", "MN1", "BBPSP");
        addContact("Chantal Gagne", "QB1", "BBPSP");
        addContact("Elizabeth-Ann Paradis", "SD1", "BBPSP");
        addContact("Dianne Dufour", "SS1", "BBPSP");
        addContact("Colleen Fitzgerald", "VN2", "BBPSP");
        addContact("Niki Davis", "HL1", "BBPSP");
        addContact("Theresa Almonte", "HM1", "BBPSP");
        addContact("Maryanne Gibson", "KN1", "BBPSP");
        addContact("Sheila Schembri", "LN1", "BBPSP");
        addContact("Nathalie Aubin", "MN2", "BBPSP");
        addContact("Lucie Lacasse", "OT1", "BBPSP");
        addContact("Tammy Burtenshaw", "OT2", "BBPSP");
        addContact("Louise Bedard", "SB1", "BBPSP");
        addContact("Janet D Sharun", "TH1", "BBPSP");
        addContact("Kathy Hjalmarsson", "WN1", "BBPSP");
        addContact("unknown", "CL2", "BBPSP");
        addContact("Tony", "TR1", "BBPSP");
        addContact("unknown", "VN1", "BBPSP");
        addContact("Dr. Neesh Pannu", "ED1", "CCCS");
        addContact("Wanda MacDonald", "ED1", "CEGIIR");
        addContact("Dellanee Kahlke", "ED1", "CHILD");
        addContact("Candace Dando", "ED1", "ERCIN");
        addContact("Daisy Gibbons", "SF1", "ERCIN");
        addContact("Dawn Opgenorth", "ED1", "KDCS");
        addContact("Ann Chala", "VN1", "KDCS");
        addContact("Sharon Gulewich", "CL1-KDCS", "KDCS");
        addContact("Bonny Granfield", "KN1", "KMS");
        addContact("Elizabeth Taylor", "ED1", "LCS");
        addContact("Bonny Granfield", "ED1", "MPS");
        addContact("Shirley Cole", "CL1-NHS", "NHS");
        addContact("Dr. Andrew Mason", "ED1", "NHS");
        addContact("Dr. Andrew Mason", "ED1", "RVS");
        addContact("Erin Rezanoff", "ED1", "SPARK");
        addContact("Sharon Gulewich", "CL1-KDCS", "TCKS");
        addContact("Sue Szigety", "ED1", "TCKS");
        addContact("Dawn Opgenorth", "ED1", "VAS");
    }

    private static StudyWrapper addStudy(SiteWrapper site, String name,
        String nameShort, String activityStatus, String comment)
        throws Exception {
        StudyWrapper study = new StudyWrapper(site.getAppService());
        study.setSite(site);
        study.setName(name);
        study.setNameShort(nameShort);
        study.setActivityStatus(activityStatus);
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

    private static void addSampleSource(String studyNameShort,
        String sampleSourceName) throws Exception {
        StudyWrapper study = getStudy(studyNameShort);
        SampleSourceWrapper ss = sampleSourceMap.get(sampleSourceName);
        if (ss == null) {
            throw new Exception("invalid sample source name: "
                + sampleSourceName);
        }
        study.addSampleSources(Arrays.asList(ss));
        study.persist();
        study.reload();
    }

    private static void addSampleStorage(String studyNameShort,
        String sampleTypeName, Integer quantity, Double volume)
        throws Exception {
        StudyWrapper study = getStudy(studyNameShort);

        SampleStorageWrapper ss = new SampleStorageWrapper(study
            .getAppService());
        ss.setSampleType(CbsrSite.getSampleType(sampleTypeName));
        ss.setQuantity(quantity);
        ss.setVolume(volume);

        study.addSampleStorages(Arrays.asList(ss));
        study.persist();
        study.reload();

    }

    private static void addPvAttr(String studyNameShort, String label,
        String type, String permissible) throws Exception {
        StudyWrapper study = getStudy(studyNameShort);
        if ((permissible != null) && (permissible.length() > 0)) {
            study.setStudyPvAttr(label, type, permissible.split(";"));
        } else {
            study.setStudyPvAttr(label, type);
        }
        study.persist();
        study.reload();
    }

    private static void addPvAttr(String studyNameShort, String label,
        String type) throws Exception {
        addPvAttr(studyNameShort, label, type, null);
    }

    private static void addContact(String contactName, String clinicName,
        String studyNameShort) throws Exception {
        ContactWrapper contact = CbsrClinics.getClinic(clinicName).getContact(
            contactName);
        if (contact == null) {
            throw new Exception("clinic " + clinicName
                + " does not have a contact with name " + contactName);
        }
        StudyWrapper study = getStudy(studyNameShort);

        study.addContacts(Arrays.asList(contact));
        study.persist();
        study.reload();
    }

}
