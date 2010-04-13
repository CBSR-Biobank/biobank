package edu.ualberta.med.biobank.common.cbsr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleStorageWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudySourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

/**
 * Query to generate studies:
 * 
 * select name, name_short, comment from study
 * 
 * Query to generate sources vessels:
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

    private static Map<String, SourceVesselWrapper> sourceVesselMap = null;

    public static void createStudies(SiteWrapper site) throws Exception {
        studiesMap = new HashMap<String, StudyWrapper>();

        sourceVesselMap = new HashMap<String, SourceVesselWrapper>();
        for (SourceVesselWrapper sourceVessel : SourceVesselWrapper
            .getAllSourceVessels(site.getAppService())) {
            sourceVesselMap.put(sourceVessel.getName(), sourceVessel);
        }

        addStudy(
            site,
            "A phase II randomized blinded controlled trial of the effect of "
                + "furoSemide in cricially ill Patients with eARly acute Kidney injury",
            "SPARK", ActivityStatusWrapper.ACTIVE_STATUS_STRING, null);
        addStudy(site, "Acute Heart Failure-Emergency Management", "AHFEM",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, null);
        addStudy(site, "Blood Borne Pathogens Surveillance Project", "BBPSP",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, null);
        addStudy(site, "Canadian Health Infant Longitudinal Development Study",
            "CHILD", ActivityStatusWrapper.ACTIVE_STATUS_STRING, null);
        addStudy(
            site,
            "Centre of Excellence for Gastrointestinal Inflammation and Immunity Research",
            "CEGIIR", ActivityStatusWrapper.ACTIVE_STATUS_STRING, null);
        addStudy(site, "Critical Care Cohort Study", "CCCS", "Closed", null);
        addStudy(site,
            "Exploring the Renoprotective effects of fluid prophylaxis "
                + "strategies for Contrast Induced Nephropathy (Study)",
            "ERCIN", "Disabled", "Precath visit - only urine is collected");
        addStudy(site, "Heart failure Etiology and Analysis Research Team",
            "HEART", ActivityStatusWrapper.ACTIVE_STATUS_STRING, null);
        addStudy(site, "Kidney Disease Cohort Study", "KDCS",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, null);
        addStudy(site, "Kingston Merger Study", "KMS",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, null);
        addStudy(
            site,
            "Laboratory Controls Study",
            "LCS",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING,
            "Created so that we could give locations/track samples that we "
                + "use as controls. Also used to store leftover aliquots of staff "
                + "blood  used in experiments");
        addStudy(site, "Man-Chui Poon Study", "MPS",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, null);
        addStudy(site, "Novartis Hepatitis C Study", "NHS",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, null);
        addStudy(site, "Retroviral Study", "RVS",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, null);
        addStudy(site, "Tonelli Chronic Kidney Study", "TCKS",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, null);
        addStudy(site, "Vascular Access Study", "VAS",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, null);

        addStudySourceVessel("AHFEM", "5mL gold top serum tube");
        addStudySourceVessel("AHFEM",
            "6ml light green top lithium heparin tube");
        addStudySourceVessel("AHFEM", "AHFEM processing pallet ");
        addStudySourceVessel("AHFEM", "EDTA cryovial");
        addStudySourceVessel("BBPSP", "10mL lavender top EDTA tube");
        addStudySourceVessel("BBPSP", "10ml orange top PAXgene tube");
        addStudySourceVessel("BBPSP", "3mL lavender top EDTA tube");
        addStudySourceVessel("BBPSP", "4ml lavender top EDTA tube");
        addStudySourceVessel("BBPSP", "urine cup");
        addStudySourceVessel("CCCS", "6mL beige top tube");
        addStudySourceVessel("CCCS", "6mL lavender top EDTA tube");
        addStudySourceVessel("CCCS", "urine cup");
        addStudySourceVessel("CEGIIR", "10mL lavender top EDTA tube");
        addStudySourceVessel("CEGIIR",
            "15ml centrifuge tube (sodium azide urine)");
        addStudySourceVessel("CEGIIR", "6mL beige top tube");
        addStudySourceVessel("CHILD", "10ml green top sodium heparin tube");
        addStudySourceVessel("CHILD", "6mL beige top tube");
        addStudySourceVessel("CHILD", "Breast milk");
        addStudySourceVessel("CHILD", "Meconium");
        addStudySourceVessel("CHILD", "Nasal Swab");
        addStudySourceVessel("CHILD", "Stool");
        addStudySourceVessel("CHILD", "urine cup");
        addStudySourceVessel("ERCIN", "ERCIN Serum processing pallet");
        addStudySourceVessel("ERCIN", "ERCIN Urine processing pallet");
        addStudySourceVessel("KDCS", "10mL lavender top EDTA tube");
        addStudySourceVessel("KDCS", "10ml orange top PAXgene tube");
        addStudySourceVessel("KDCS",
            "15ml centrifuge tube (sodium azide urine)");
        addStudySourceVessel("KDCS", "3mL lavender top EDTA tube");
        addStudySourceVessel("KDCS", "3mL red top tube (hemodialysate)");
        addStudySourceVessel("KDCS", "3ml red top tube (source water)");
        addStudySourceVessel("KDCS", "6mL beige top tube");
        addStudySourceVessel("KDCS", "6ml beige top tube (tap water)");
        addStudySourceVessel("KDCS", "fingernail tube");
        addStudySourceVessel("KDCS", "hair bagette");
        addStudySourceVessel("KDCS", "toenail tube");
        addStudySourceVessel("KMS", "EDTA cryovial");
        addStudySourceVessel("LCS", "10mL lavender top EDTA tube");
        addStudySourceVessel("LCS", "10ml green top sodium heparin tube");
        addStudySourceVessel("LCS", "10ml orange top PAXgene tube");
        addStudySourceVessel("LCS", "15ml centrifuge tube (ascites fluid)");
        addStudySourceVessel("LCS", "15ml centrifuge tube (sodium azide urine)");
        addStudySourceVessel("LCS", "2.7mL blue top Sodium citrate tube");
        addStudySourceVessel("LCS", "3mL lavender top EDTA tube");
        addStudySourceVessel("LCS", "3mL red top tube (hemodialysate)");
        addStudySourceVessel("LCS", "3ml red top tube (source water)");
        addStudySourceVessel("LCS", "4.5mL blue top Sodium citrate tube");
        addStudySourceVessel("LCS", "4ml lavender top EDTA tube");
        addStudySourceVessel("LCS", "5mL gold top serum tube");
        addStudySourceVessel("LCS", "6mL beige top tube");
        addStudySourceVessel("LCS", "6mL lavender top EDTA tube");
        addStudySourceVessel("LCS", "6ml beige top tube (tap water)");
        addStudySourceVessel("LCS", "6ml light green top lithium heparin tube");
        addStudySourceVessel("LCS", "AHFEM processing pallet ");
        addStudySourceVessel("LCS", "Breast milk");
        addStudySourceVessel("LCS", "EDTA cryovial");
        addStudySourceVessel("LCS", "ERCIN Serum processing pallet");
        addStudySourceVessel("LCS", "ERCIN Urine processing pallet");
        addStudySourceVessel("LCS", "Meconium");
        addStudySourceVessel("LCS", "N/A");
        addStudySourceVessel("LCS", "Nasal Swab");
        addStudySourceVessel("LCS", "Stool");
        addStudySourceVessel("LCS", "fingernail tube");
        addStudySourceVessel("LCS", "hair bagette");
        addStudySourceVessel("LCS", "toenail tube");
        addStudySourceVessel("LCS", "urine cup");
        addStudySourceVessel("MPS", "2.7mL blue top Sodium citrate tube");
        addStudySourceVessel("NHS", "4ml lavender top EDTA tube");
        addStudySourceVessel("NHS", "5mL gold top serum tube");
        addStudySourceVessel("RVS", "10mL lavender top EDTA tube");
        addStudySourceVessel("RVS", "10ml orange top PAXgene tube");
        addStudySourceVessel("RVS", "15ml centrifuge tube (sodium azide urine)");
        addStudySourceVessel("RVS", "5mL gold top serum tube");
        addStudySourceVessel("SPARK", "4ml lavender top EDTA tube");
        addStudySourceVessel("SPARK", "urine cup");
        addStudySourceVessel("TCKS", "10mL lavender top EDTA tube");
        addStudySourceVessel("TCKS", "10ml orange top PAXgene tube");
        addStudySourceVessel("TCKS",
            "15ml centrifuge tube (sodium azide urine)");
        addStudySourceVessel("TCKS", "6mL beige top tube");
        addStudySourceVessel("TCKS", "fingernail tube");
        addStudySourceVessel("TCKS", "hair bagette");
        addStudySourceVessel("TCKS", "toenail tube");
        addStudySourceVessel("VAS", "10mL lavender top EDTA tube");
        addStudySourceVessel("VAS", "10ml orange top PAXgene tube");
        addStudySourceVessel("VAS", "5mL gold top serum tube");
        addStudySourceVessel("VAS", "6mL beige top tube");

        addSampleStorage("AHFEM", "Lith Hep Plasma", 20, 0.2);
        addSampleStorage("AHFEM", "PF Plasma", 2, 0.2);
        addSampleStorage("AHFEM", "Plasma", 5, 0.2);
        addSampleStorage("AHFEM", "Serum", 5, 0.2);
        addSampleStorage("BBPSP", "CDPA Plasma", 3, 1.0);
        addSampleStorage("BBPSP", "Cells", null, null);
        addSampleStorage("BBPSP", "DNA(Blood)", 2, 0.4);
        addSampleStorage("BBPSP", "DNA (WBC)", null, null);
        addSampleStorage("BBPSP", "F Urine", null, null);
        addSampleStorage("BBPSP", "Paxgene", 11, 0.8);
        addSampleStorage("BBPSP", "Plasma", 16, 0.2);
        addSampleStorage("BBPSP", "Urine", null, null);
        addSampleStorage("CCCS", "C Urine", 6, 0.9);
        addSampleStorage("CCCS", "Plasma", 6, 0.4);
        addSampleStorage("CCCS", "Serum B", 6, 0.9);
        addSampleStorage("CCCS", "WBE", 6, 0.9);
        addSampleStorage("CCCS", "Z Urine", 6, 0.9);
        addSampleStorage("CEGIIR", "Cells", 5, 0.5);
        addSampleStorage("CEGIIR", "DNA(Blood)", 2, 0.4);
        addSampleStorage("CEGIIR", "Plasma", 5, 0.4);
        addSampleStorage("CEGIIR", "Serum B", 6, 0.8);
        addSampleStorage("CEGIIR", "WB DMSO", 2, 0.8);
        addSampleStorage("CEGIIR", "WBE", null, null);
        addSampleStorage("CEGIIR", "Colon, A", null, null);
        addSampleStorage("CEGIIR", "Colon, D", null, null);
        addSampleStorage("CEGIIR", "Duodenum", null, null);
        addSampleStorage("CEGIIR", "Ileum", null, null);
        addSampleStorage("CEGIIR", "Jejunum", null, null);
        addSampleStorage("CEGIIR", "R-ColonA", null, null);
        addSampleStorage("CEGIIR", "R-ColonD", null, null);
        addSampleStorage("CEGIIR", "R-Duodenum", null, null);
        addSampleStorage("CEGIIR", "R-Ilieum", null, null);
        addSampleStorage("CEGIIR", "R-StomachA", null, null);
        addSampleStorage("CEGIIR", "R-StomachB", null, null);
        addSampleStorage("CEGIIR", "R-ColonT", null, null);
        addSampleStorage("CEGIIR", "Stomach, A", null, null);
        addSampleStorage("CEGIIR", "Stomach, B", null, null);
        addSampleStorage("CEGIIR", "Colon, T", null, null);
        addSampleStorage("CEGIIR", "Z Urine", 6, 0.9);
        addSampleStorage("CHILD", "CBMC RNA", null, null);
        addSampleStorage("CHILD", "CBMC", null, null);
        addSampleStorage("CHILD", "Cells", null, null);
        addSampleStorage("CHILD", "Meconium", null, null);
        addSampleStorage("CHILD", "Plasma SH", null, null);
        addSampleStorage("CHILD", "Serum B", null, null);
        addSampleStorage("CHILD", "Serum Pel", null, null);
        addSampleStorage("CHILD", "Serum", null, null);
        addSampleStorage("CHILD", "WB Plasma", null, null);
        addSampleStorage("CHILD", "WB RNA", null, null);
        addSampleStorage("CHILD", "WB Serum", null, null);
        addSampleStorage("CHILD", "WBlood", null, null);
        addSampleStorage("ERCIN", "C Urine", 6, 0.9);
        addSampleStorage("ERCIN", "Serum B", 12, 0.4);
        addSampleStorage("ERCIN", "Serum", null, null);
        addSampleStorage("ERCIN", "Z Urine", 12, 0.9);
        addSampleStorage("HEART", "DNA E 1000", null, null);
        addSampleStorage("HEART", "DNA L 1000", null, null);
        addSampleStorage("HEART", "LH PFP 200", null, null);
        addSampleStorage("HEART", "LH PFP 500", null, null);
        addSampleStorage("HEART", "P100 500", null, null);
        addSampleStorage("HEART", "PlasmaE200", null, null);
        addSampleStorage("HEART", "PlasmaE500", null, null);
        addSampleStorage("HEART", "PlasmaE800", null, null);
        addSampleStorage("HEART", "PlasmaL200", null, null);
        addSampleStorage("HEART", "PlasmaL500", null, null);
        addSampleStorage("HEART", "SerumG500", null, null);
        addSampleStorage("HEART", "UrineC900", null, null);
        addSampleStorage("HEART", "UrineSA900", null, null);
        addSampleStorage("KDCS", "Cells", 10, 0.4);
        addSampleStorage("KDCS", "DNA(Blood)", 2, 0.4);
        addSampleStorage("KDCS", "Dialysate", 3, 0.4);
        addSampleStorage("KDCS", "F Nails", null, null);
        addSampleStorage("KDCS", "Hair", null, null);
        addSampleStorage("KDCS", "Paxgene", 11, 0.8);
        addSampleStorage("KDCS", "Plasma", 12, 0.4);
        addSampleStorage("KDCS", "S Water", 3, 0.9);
        addSampleStorage("KDCS", "Serum B", 8, 0.4);
        addSampleStorage("KDCS", "Serum", null, null);
        addSampleStorage("KDCS", "T Nails", null, null);
        addSampleStorage("KDCS", "T Water", 3, 0.9);
        addSampleStorage("KDCS", "Urine", null, null);
        addSampleStorage("KDCS", "WBE", 4, 0.8);
        addSampleStorage("KDCS", "Z Urine", 3, 0.4);
        addSampleStorage("KMS", "Plasma", 20, 0.1);
        addSampleStorage("MPS", "Plasma", 3, 1.0);
        addSampleStorage("NHS", "Cells", 10, 0.5);
        addSampleStorage("NHS", "Plasma", 12, 0.4);
        addSampleStorage("NHS", "Serum", 16, 0.4);
        addSampleStorage("RVS", "Cells", 10, 0.5);
        addSampleStorage("RVS", "Paxgene", 22, 0.8);
        addSampleStorage("RVS", "Plasma", 16, 0.4);
        addSampleStorage("RVS", "Serum", 4, 0.4);
        addSampleStorage("RVS", "Z Urine", 6, 0.9);
        addSampleStorage("SPARK", "C Urine", 5, 0.5);
        addSampleStorage("SPARK", "Plasma", 5, 0.5);
        addSampleStorage("SPARK", "WB DMSO", 1, 0.5);
        addSampleStorage("TCKS", "Cells", 10, 0.5);
        addSampleStorage("TCKS", "DNA(Blood)", 2, 0.4);
        addSampleStorage("TCKS", "F Nails", null, null);
        addSampleStorage("TCKS", "Hair", null, null);
        addSampleStorage("TCKS", "Paxgene", 11, 0.8);
        addSampleStorage("TCKS", "Plasma", 12, 0.4);
        addSampleStorage("TCKS", "Serum B", 8, 0.4);
        addSampleStorage("TCKS", "T Nails", null, null);
        addSampleStorage("TCKS", "Urine", null, null);
        addSampleStorage("TCKS", "WBE", 4, 0.8);
        addSampleStorage("TCKS", "Z Urine", 3, 0.4);
        addSampleStorage("VAS", "Cells", 10, 0.5);
        addSampleStorage("VAS", "DNA(Blood)", 2, 0.4);
        addSampleStorage("VAS", "Dialysate", null, null);
        addSampleStorage("VAS", "F Nails", null, null);
        addSampleStorage("VAS", "Hair", null, null);
        addSampleStorage("VAS", "Paxgene", 11, 0.8);
        addSampleStorage("VAS", "Plasma", 12, 0.4);
        addSampleStorage("VAS", "Serum B", 8, 0.4);
        addSampleStorage("VAS", "Serum", null, null);
        addSampleStorage("VAS", "T Nails", null, null);
        addSampleStorage("VAS", "WBE", 4, 0.8);

        addPvAttr(site, "AHFEM", "PBMC Count (x10^6)", "number");
        addPvAttr(site, "AHFEM", "Worksheet", "text");
        addPvAttr(site, "BBPSP", "Consent", "select_multiple",
            "Surveillance;Genetic Predisposition;Previous Samples;Genetic Mutation");
        addPvAttr(site, "BBPSP", "PBMC Count (x10^6)", "number");
        addPvAttr(site, "BBPSP", "Phlebotomist", "text");
        addPvAttr(site, "BBPSP", "Worksheet", "text");
        addPvAttr(site, "CCCS", "PBMC Count (x10^6)", "number");
        addPvAttr(site, "CCCS", "Worksheet", "text");
        addPvAttr(site, "CEGIIR", "PBMC Count (x10^6)", "number");
        addPvAttr(site, "CEGIIR", "Worksheet", "text");
        addPvAttr(site, "CHILD", "PBMC Count (x10^6)", "number");
        addPvAttr(site, "CHILD", "Worksheet", "text");
        addPvAttr(site, "ERCIN", "PBMC Count (x10^6)", "number");
        addPvAttr(site, "ERCIN", "Visit Type", "select_single",
            "Baseline;Precath;6hr Post;24hr Post;48-72hr Post");
        addPvAttr(site, "ERCIN", "Worksheet", "text");
        addPvAttr(site, "KDCS", "Consent", "select_multiple", "Genetic");
        addPvAttr(site, "KDCS", "PBMC Count (x10^6)", "number");
        addPvAttr(site, "KDCS", "Worksheet", "text");
        addPvAttr(site, "KMS", "PBMC Count (x10^6)", "number");
        addPvAttr(site, "KMS", "Worksheet", "text");
        addPvAttr(site, "LCS", "PBMC Count (x10^6)", "number");
        addPvAttr(site, "LCS", "Worksheet", "text");
        addPvAttr(site, "MPS", "PBMC Count (x10^6)", "number");
        addPvAttr(site, "MPS", "Worksheet", "text");
        addPvAttr(site, "NHS", "Biopsy Length", "number");
        addPvAttr(site, "NHS", "PBMC Count (x10^6)", "number");
        addPvAttr(site, "NHS", "Visit Type", "select_single",
            "D0;D2;D4;Wk2;Wk4;M2;M8;M12;M18;M24");
        addPvAttr(site, "NHS", "Worksheet", "text");
        addPvAttr(site, "RVS", "PBMC Count (x10^6)", "number");
        addPvAttr(site, "RVS", "Worksheet", "text");
        addPvAttr(site, "SPARK", "PBMC Count (x10^6)", "number");
        addPvAttr(site, "SPARK", "Worksheet", "text");
        addPvAttr(site, "TCKS", "PBMC Count (x10^6)", "number");
        addPvAttr(site, "TCKS", "Worksheet", "text");
        addPvAttr(site, "VAS", "PBMC Count (x10^6)", "number");
        addPvAttr(site, "VAS", "Worksheet", "text");

        addContact("Aleasha Warner", "HL2", "BBPSP");
        addContact("Ann Chala", "VN1", "KDCS");
        addContact("Bonny Granfield", "ED1", "MPS");
        addContact("Bonny Granfield", "KN1", "KMS");
        addContact("Candace Dando", "ED1", "ERCIN");
        addContact("Chantal Gagne", "QB1", "BBPSP");
        addContact("Colleen Fitzgerald", "VN2", "BBPSP");
        addContact("Daisy Gibbons", "SF1", "ERCIN");
        addContact("Dawn Opgenorth", "ED1", "KDCS");
        addContact("Dawn Opgenorth", "ED1", "VAS");
        addContact("Dellanee Kahlke", "ED1", "CHILD");
        addContact("Dianne Dufour", "SS1", "BBPSP");
        addContact("Dorine Belliveau", "MC1", "BBPSP");
        addContact("Dr. Andrew Mason", "ED1", "NHS");
        addContact("Dr. Andrew Mason", "ED1", "RVS");
        addContact("Dr. Justin Ezekowitz", "ED1", "AHFEM");
        addContact("Dr. Neesh Pannu", "ED1", "CCCS");
        addContact("Elaine Gloutnez", "MN1", "BBPSP");
        addContact("Elizabeth Taylor", "ED1", "HEART");
        addContact("Elizabeth Taylor", "ED1", "LCS");
        addContact("Elizabeth-Ann Paradis", "SD1", "BBPSP");
        addContact("Erin Rezanoff", "ED1", "SPARK");
        addContact("Janet D Sharun", "TH1", "BBPSP");
        addContact("Kathy Hjalmarsson", "WN1", "BBPSP");
        addContact("Louise Bedard", "SB1", "BBPSP");
        addContact("Lucie Lacasse", "OT1", "BBPSP");
        addContact("Maryanne Gibson", "KN1", "BBPSP");
        addContact("Melanie Peters", "ED1", "BBPSP");
        addContact("Morna Brown", "CL1", "BBPSP");
        addContact("Nathalie Aubin", "MN2", "BBPSP");
        addContact("Niki Davis", "HL1", "BBPSP");
        addContact("Sharon Gulewich", "CL1-KDCS", "KDCS");
        addContact("Sharon Gulewich", "CL1-KDCS", "TCKS");
        addContact("Sheila Schembri", "LN1", "BBPSP");
        addContact("Shirley Cole", "CL1-NHS", "NHS");
        addContact("Sue Szigety", "ED1", "TCKS");
        addContact("Tammy Burtenshaw", "OT2", "BBPSP");
        addContact("Theresa Almonte", "HM1", "BBPSP");
        addContact("Tony", "TR1", "BBPSP");
        addContact("Wanda MacDonald", "ED1", "CEGIIR");
        addContact("unknown", "CL2", "BBPSP");
        addContact("unknown", "VN1", "BBPSP");
    }

    private static StudyWrapper addStudy(SiteWrapper site, String name,
        String nameShort, String activityStatusName, String comment)
        throws Exception {
        StudyWrapper study = new StudyWrapper(site.getAppService());
        study.setSite(site);
        study.setName(name);
        study.setNameShort(nameShort);
        study.setActivityStatus(CbsrSite.getActivityStatus(activityStatusName));
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

    private static void addStudySourceVessel(String studyNameShort, String name)
        throws Exception {
        StudyWrapper study = getStudy(studyNameShort);
        SourceVesselWrapper ss = sourceVesselMap.get(name);
        if (ss == null) {
            throw new Exception("invalid source vessel name: " + name);
        }
        StudySourceVesselWrapper studySourceVessel = new StudySourceVesselWrapper(
            ss.getAppService());
        studySourceVessel.setSourceVessel(ss);
        study.addStudySourceVessels(Arrays.asList(studySourceVessel));
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
        ss.setActivityStatus(CbsrSite.getActiveActivityStatus());

        study.addSampleStorage(Arrays.asList(ss));
        study.persist();
        study.reload();

    }

    private static void addPvAttr(SiteWrapper site, String studyNameShort,
        String label, String type, String permissible) throws Exception {
        StudyWrapper study = getStudy(studyNameShort);
        if ((permissible != null) && (permissible.length() > 0)) {
            study.setStudyPvAttr(label, type, permissible.split(";"));
        } else {
            study.setStudyPvAttr(label, type);
        }
        study.setStudyPvAttrActivityStatus(label, ActivityStatusWrapper
            .getActivityStatus(study.getAppService(),
                ActivityStatusWrapper.ACTIVE_STATUS_STRING));
        study.persist();
        study.reload();

        List<String> sitePvAttrs = SiteWrapper.getPvAttrTypeNames(site
            .getAppService());
        if (!sitePvAttrs.contains(label)) {
            // add this pv attr to the site
            site.setSitePvAttr(label, type);
            site.persist();
            site.reload();
        }
    }

    private static void addPvAttr(SiteWrapper site, String studyNameShort,
        String label, String type) throws Exception {
        addPvAttr(site, studyNameShort, label, type, null);
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
