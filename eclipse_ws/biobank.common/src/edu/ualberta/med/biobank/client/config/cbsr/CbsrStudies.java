package edu.ualberta.med.biobank.client.config.cbsr;

import java.util.HashMap;

import edu.ualberta.med.biobank.client.config.ConfigStudies;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceVesselWrapper;
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
public class CbsrStudies extends ConfigStudies {

    public CbsrStudies(SiteWrapper site) throws Exception {
        super(site);
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
        addStudy(site, "Critical Care Cohort Study", "CCCS",
            ActivityStatusWrapper.CLOSED_STATUS_STRING, null);

        addStudy(
            site,
            "Enzyme replacement therapy in patients with Fabry disease: differential impact on Heart Remodeling and Vascular Function",
            "FABRY", ActivityStatusWrapper.ACTIVE_STATUS_STRING,
            "Study starts with BA for Edmonton, BB for Calgary");

        addStudy(site,
            "Exploring the Renoprotective effects of fluid prophylaxis "
                + "strategies for Contrast Induced Nephropathy (Study)",
            "ERCIN", ActivityStatusWrapper.CLOSED_STATUS_STRING,
            "Precath visit - only urine is collected");
        addStudy(site, "Fedorak Iron Deficiency Study", "FIDS",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, null);
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
        addStudySourceVessel("AHFEM", "AHFEM processing pallet");
        addStudySourceVessel("AHFEM", "EDTA cryovial");
        addStudySourceVessel("BBPSP", "10mL lavender top EDTA tube");
        addStudySourceVessel("BBPSP", "10ml orange top PAXgene tube");
        addStudySourceVessel("BBPSP", "3mL lavender top EDTA tube");
        addStudySourceVessel("BBPSP", "4ml lavender top EDTA tube");
        addStudySourceVessel("BBPSP", "6mL lavender top EDTA tube");
        addStudySourceVessel("BBPSP", "9ml CPDA yellow top tube");

        addStudySourceVessel("BBPSP", "urine cup");
        addStudySourceVessel("CCCS", "6mL beige top tube");
        addStudySourceVessel("CCCS", "6mL lavender top EDTA tube");
        addStudySourceVessel("CCCS", "urine cup");
        addStudySourceVessel("CEGIIR", "10mL lavender top EDTA tube");
        addStudySourceVessel("CEGIIR",
            "15ml centrifuge tube (sodium azide urine)");
        addStudySourceVessel("CEGIIR", "6mL beige top tube");
        addStudySourceVessel("CEGIIR", "Colonoscopy Kit");
        addStudySourceVessel("CEGIIR", "Enteroscopy Kit");
        addStudySourceVessel("CEGIIR", "Gastroscopy Kit");

        addStudySourceVessel("CHILD", "10ml green top sodium heparin tube");
        addStudySourceVessel("CHILD", "6mL beige top tube");
        addStudySourceVessel("CHILD", "Breast milk");
        addStudySourceVessel("CHILD", "Meconium");
        addStudySourceVessel("CHILD", "Nasal Swab");
        addStudySourceVessel("CHILD", "Stool");
        addStudySourceVessel("CHILD", "urine cup");
        addStudySourceVessel("ERCIN", "ERCIN Serum processing pallet");
        addStudySourceVessel("ERCIN", "ERCIN Urine processing pallet");

        addStudySourceVessel("FABRY", "10ml green top Lithium Heparin tube");
        addStudySourceVessel("FABRY", "10mL lavender top EDTA tube");
        addStudySourceVessel("FABRY", "10ml orange top PAXgene tube");
        addStudySourceVessel("FABRY", "5mL gold top serum tube");
        addStudySourceVessel("FABRY", "8.5ml P100 orange top tube");
        addStudySourceVessel("FIDS", "4ml lavender top EDTA tube");

        addStudySourceVessel("HEART", "10ml green top Lithium Heparin tube");
        addStudySourceVessel("HEART", "10mL lavender top EDTA tube");
        addStudySourceVessel("HEART", "10ml orange top PAXgene tube");
        addStudySourceVessel("HEART", "5mL gold top serum tube");
        addStudySourceVessel("HEART", "6mL lavender top EDTA tube");
        addStudySourceVessel("HEART", "8.5ml P100 orange top tube");
        addStudySourceVessel("HEART", "urine cup");

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
        addStudySourceVessel("LCS", "AHFEM processing pallet");
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
        addStudySourceVessel("NHS", "Biopsy, RNA later");

        addStudySourceVessel("RVS", "10mL lavender top EDTA tube");
        addStudySourceVessel("RVS", "10ml orange top PAXgene tube");
        addStudySourceVessel("RVS", "15ml centrifuge tube (sodium azide urine)");
        addStudySourceVessel("RVS", "5mL gold top serum tube");
        addStudySourceVessel("RVS", "Biopsy, RNA later");

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
        addStudySourceVessel("VAS", "6mL beige top tube");

        addSampleStorage("AHFEM", "Lith Hep Plasma", "25", "0.2",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("AHFEM", "PF Plasma", "2", "0.2",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("AHFEM", "Plasma", "10", "0.2",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("AHFEM", "Serum", "10", "0.2",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("BBPSP", "CDPA Plasma", "3", "1",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("BBPSP", "Cells", "10", "0.5",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("BBPSP", "DNA (WBC)", "2", "0.4",
            ActivityStatusWrapper.CLOSED_STATUS_STRING);
        addSampleStorage("BBPSP", "DNA(Blood)", "2", "0.4",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("BBPSP", "F Urine", "10", "0.9",
            ActivityStatusWrapper.CLOSED_STATUS_STRING);
        addSampleStorage("BBPSP", "Paxgene", "11", "0.8",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("BBPSP", "Plasma", "16", "0.2",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("BBPSP", "Urine", "10", "0.9",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("CCCS", "C Urine", "6", "0.9",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("CCCS", "Plasma", "6", "0.4",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("CCCS", "Serum B", "6", "0.9",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("CCCS", "WBE", "6", "0.9",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("CCCS", "Z Urine", "6", "0.9",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("CEGIIR", "Cells", "5", "0.5",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("CEGIIR", "Colon, A", "2", "0",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("CEGIIR", "Colon, D", "2", "0",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("CEGIIR", "Colon, T", "2", "0",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("CEGIIR", "DNA(Blood)", "2", "0.4",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("CEGIIR", "Duodenum", "2", "0",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("CEGIIR", "Ileum", "2", "0",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("CEGIIR", "Plasma", "5", "0.4",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("CEGIIR", "Serum B", "6", "0.8",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("CEGIIR", "Stomach, A", "2", "0",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("CEGIIR", "Stomach, B", "2", "0",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("CEGIIR", "WB DMSO", "2", "0.8",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("CEGIIR", "WBE", "2", "0.8",
            ActivityStatusWrapper.CLOSED_STATUS_STRING);
        addSampleStorage("CEGIIR", "Z Urine", "6", "0.9",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);

        addSampleStorage("CEGIIR", "R-ColonA", "1", "0.2",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("CEGIIR", "R-ColonD", "1", "0.2",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("CEGIIR", "R-ColonT", "1", "0.2",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("CEGIIR", "R-Duodenum", "1", "0.2",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("CEGIIR", "R-Ilieum", "1", "0.2",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("CEGIIR", "R-Jejunum", "1", "0.2",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("CEGIIR", "R-StomachA", "1", "0.2",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("CEGIIR", "R-StomachB", "1", "0.2",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("CEGIIR", "Jejunum", "2", "0",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);

        addSampleStorage("CHILD", "CBMC", null, null,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("CHILD", "CBMC RNA", null, null,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("CHILD", "Cells", null, null,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("CHILD", "Meconium", null, null,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("CHILD", "Plasma SH", null, null,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("CHILD", "Serum", null, null,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("CHILD", "Serum B", null, null,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("CHILD", "Serum Pel", null, null,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("CHILD", "WB Plasma", null, null,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("CHILD", "WB RNA", null, null,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("CHILD", "WB Serum", null, null,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("CHILD", "WBlood", null, null,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("ERCIN", "C Urine", "6", "0.9",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("ERCIN", "Serum", "12", "0.4",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("ERCIN", "Serum B", "12", "0.4",
            ActivityStatusWrapper.CLOSED_STATUS_STRING);
        addSampleStorage("ERCIN", "Z Urine", "12", "0.9",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);

        addSampleStorage("FABRY", "LH PFP 200", "10", "0.2",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("FABRY", "LH PFP 500", "5", "0.5",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("FABRY", "P100 500", "8", "0.5",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("FABRY", "PlasmaE200", "10", "0.2",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("FABRY", "PlasmaE500", "5", "0.5",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("FABRY", "SerumG500", "5", "0.5",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);

        addSampleStorage("FIDS", "PlasmaE250", "1", "0.25",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("FIDS", "PlasmaE500", "1", "0.5",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("FIDS", "UrineSA700", "3", "0.7",
            ActivityStatusWrapper.CLOSED_STATUS_STRING);

        addSampleStorage("HEART", "DNA E 1000", "5", "1",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("HEART", "DNA L 1000", "5", "1",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("HEART", "LH PFP 200", "10", "0.2",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("HEART", "LH PFP 500", "5", "0.5",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("HEART", "P100 500", "8", "0.5",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("HEART", "PlasmaE200", "10", "0.2",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("HEART", "PlasmaE500", "5", "0.5",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("HEART", "PlasmaE800", "3", "0.8",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("HEART", "PlasmaL200", "10", "0.2",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("HEART", "PlasmaL500", "5", "0.5",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("HEART", "SerumG500", "5", "0.5",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("HEART", "UrineC900", "6", "0.9",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("HEART", "UrineSA900", "12", "0.9",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("KDCS", "Cells", "10", "0.4",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("KDCS", "Dialysate", "3", "0.4",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("KDCS", "DNA(Blood)", "2", "0.4",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("KDCS", "F Nails", null, null,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("KDCS", "Hair", null, null,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("KDCS", "Paxgene", "11", "0.8",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("KDCS", "Plasma", "12", "0.4",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("KDCS", "S Water", "3", "0.9",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("KDCS", "Serum", "8", "0.4",
            ActivityStatusWrapper.CLOSED_STATUS_STRING);
        addSampleStorage("KDCS", "Serum B", "8", "0.4",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("KDCS", "T Nails", null, null,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("KDCS", "T Water", "3", "0.9",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("KDCS", "Urine", null, null,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("KDCS", "WBE", "4", "0.8",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("KDCS", "Z Urine", "3", "0.4",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("KMS", "Plasma", "20", "0.1",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);

        addSampleStorage("LCS", "BC", "5", "0.4",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("LCS", "Colon, T", "20", "0.5",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);

        addSampleStorage("MPS", "Plasma", "3", "1",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("NHS", "Cells", "10", "0.5",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("NHS", "Plasma", "12", "0.4",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("NHS", "Serum", "16", "0.4",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("RVS", "Cells", "10", "0.5",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("RVS", "Paxgene", "22", "0.8",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("RVS", "Plasma", "16", "0.4",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("RVS", "Serum", "4", "0.4",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("RVS", "Z Urine", "6", "0.9",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("SPARK", "C Urine", "5", "0.5",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("SPARK", "Plasma", "5", "0.5",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("SPARK", "WB DMSO", "1", "0.5",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("TCKS", "Cells", "10", "0.5",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("TCKS", "DNA(Blood)", "2", "0.4",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("TCKS", "F Nails", null, null,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("TCKS", "Hair", null, null,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("TCKS", "Paxgene", "11", "0.8",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("TCKS", "Plasma", "12", "0.4",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("TCKS", "Serum B", "8", "0.4",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("TCKS", "T Nails", null, null,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("TCKS", "Urine", null, null,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("TCKS", "WBE", "4", "0.8",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("TCKS", "Z Urine", "3", "0.4",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("VAS", "Cells", "10", "0.5",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("VAS", "Dialysate", null, null,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("VAS", "DNA(Blood)", "2", "0.4",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("VAS", "F Nails", null, null,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("VAS", "Hair", null, null,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("VAS", "Paxgene", "11", "0.8",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("VAS", "Plasma", "12", "0.4",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("VAS", "Serum", "8", "0.4",
            ActivityStatusWrapper.CLOSED_STATUS_STRING);
        addSampleStorage("VAS", "Serum B", "8", "0.4",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("VAS", "T Nails", null, null,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        addSampleStorage("VAS", "WBE", "4", "0.8",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);

        addPvAttr("AHFEM", "PBMC Count (x10^6)", "number");
        addPvAttr("AHFEM", "Worksheet", "text");
        addPvAttr("BBPSP", "Consent", "select_multiple",
            "Surveillance;Genetic Predisposition;Previous Samples;Genetic Mutation");
        addPvAttr("BBPSP", "PBMC Count (x10^6)", "number");
        addPvAttr("BBPSP", "Phlebotomist", "text");
        addPvAttr("BBPSP", "Worksheet", "text");
        addPvAttr("CCCS", "PBMC Count (x10^6)", "number");
        addPvAttr("CCCS", "Worksheet", "text");
        addPvAttr("CEGIIR", "PBMC Count (x10^6)", "number");
        addPvAttr("CEGIIR", "Worksheet", "text");
        addPvAttr("CHILD", "PBMC Count (x10^6)", "number");
        addPvAttr("CHILD", "Worksheet", "text");
        addPvAttr("ERCIN", "PBMC Count (x10^6)", "number");
        addPvAttr("ERCIN", "Visit Type", "select_single",
            "Baseline;Precath;6hr Post;24hr Post;48-72hr Post");
        addPvAttr("ERCIN", "Worksheet", "text");
        addPvAttr("KDCS", "Consent", "select_multiple", "Genetic");
        addPvAttr("KDCS", "PBMC Count (x10^6)", "number");
        addPvAttr("KDCS", "Worksheet", "text");
        addPvAttr("KMS", "PBMC Count (x10^6)", "number");
        addPvAttr("KMS", "Worksheet", "text");
        addPvAttr("LCS", "PBMC Count (x10^6)", "number");
        addPvAttr("LCS", "Worksheet", "text");
        addPvAttr("MPS", "PBMC Count (x10^6)", "number");
        addPvAttr("MPS", "Worksheet", "text");
        addPvAttr("NHS", "Biopsy Length", "number");
        addPvAttr("NHS", "PBMC Count (x10^6)", "number");
        addPvAttr("NHS", "Visit Type", "select_single",
            "D0;D2;D4;Wk2;Wk4;M2;M8;M12;M18;M24");
        addPvAttr("NHS", "Worksheet", "text");
        addPvAttr("RVS", "PBMC Count (x10^6)", "number");
        addPvAttr("RVS", "Worksheet", "text");
        addPvAttr("SPARK", "PBMC Count (x10^6)", "number");
        addPvAttr("SPARK", "Worksheet", "text");
        addPvAttr("TCKS", "PBMC Count (x10^6)", "number");
        addPvAttr("TCKS", "Worksheet", "text");
        addPvAttr("VAS", "PBMC Count (x10^6)", "number");
        addPvAttr("VAS", "Worksheet", "text");

        addContact("AHFEM", "Dr. Justin Ezekowitz", "ED1-UofA");
        addContact("BBPSP", "Aleasha Warner", "HL2-IWK");
        addContact("BBPSP", "Chantal Gagne", "QB1-Enfant-Jesus");
        addContact("BBPSP", "Colleen Fitzgerald", "VN2-Childrens Hosp");
        addContact("BBPSP", "Dianne Dufour", "SS1-Royal Hosp");
        addContact("BBPSP", "Dorine Belliveau", "MC1-Moncton Hosp");
        addContact("BBPSP", "Elaine Gloutnez", "MN1-Ste-Justine");
        addContact("BBPSP", "Elizabeth-Ann Paradis", "SD1-Sudbury Hosp");
        addContact("BBPSP", "Janet D Sharun", "TH1-Regional Hosp");
        addContact("BBPSP", "Kathy Hjalmarsson", "WN1-Cancer Care");
        addContact("BBPSP", "Louise Bedard", "SB1-St John NB Hosp");
        addContact("BBPSP", "Lucie Lacasse", "OT1-Ottawa Hosp");
        addContact("BBPSP", "Maryanne Gibson", "KN1-Cancer Ctr");
        addContact("BBPSP", "Melanie Peters", "ED1-UofA");
        addContact("BBPSP", "Morna Brown", "CL1-Foothills");
        addContact("BBPSP", "Nathalie Aubin", "MN2-Children Hosp");
        addContact("BBPSP", "Niki Davis", "HL1-QE II");
        addContact("BBPSP", "Sheila Schembri", "LN1-St Joseph");
        addContact("BBPSP", "Tammy Burtenshaw", "OT2-Children Hosp");
        addContact("BBPSP", "Theresa Almonte", "HM1-McMaster");
        addContact("BBPSP", "Tony", "TR1-St Mikes");
        addContact("BBPSP", "unknown", "CL2-Children Hosp");
        addContact("BBPSP", "unknown", "VN1-St Paul");
        addContact("CCCS", "Dr. Neesh Pannu", "ED1-UofA");
        addContact("CEGIIR", "Wanda MacDonald", "ED1-UofA");
        addContact("CHILD", "Dellanee Kahlke", "ED1-UofA");
        addContact("ERCIN", "Candace Dando", "ED1-UofA");
        addContact("ERCIN", "Daisy Gibbons", "SF1-Health NFLD");

        addContact("FABRY", "Marleen Irwin", "ED1-UofA");
        addContact("FIDS", "Dr. Thomas Lee", "ED1-UofA");
        addContact("FIDS", "Rae Foshaug", "ED1-UofA");

        addContact("HEART", "Marleen Irwin", "ED1-UofA");

        addContact("KDCS", "Ann Chala", "VN1-St Paul");
        addContact("KDCS", "Dawn Opgenorth", "ED1-UofA");
        addContact("KDCS", "Sharon Gulewich", "CL1-Sunridge");
        addContact("KDCS", "Cathy Lent", "WL1-Westlock Hosp");
        addContact("KDCS", "Gwen Winter", "RD1-Red Deer Hosp");
        addContact("KDCS", "Janilee Dow", "LM1-Lloyd Hosp");
        addContact("KDCS", "Jodie Hingst", "OL1-Hingst");
        addContact("KDCS", "Phillo King", "FM1-King");
        addContact("KDCS", "Sharon Mollins", "GP1-QE Hosp");
        addContact("KDCS", "Stacey Culp", "SP1-St Therese Hosp");

        addContact("KMS", "Bonny Granfield", "KN1-Cancer Ctr");
        addContact("LCS", "Elizabeth Taylor", "ED1-UofA");
        addContact("MPS", "Bonny Granfield", "ED1-UofA");
        addContact("NHS", "Dr. Andrew Mason", "ED1-UofA");
        addContact("NHS", "Shirley Cole", "CL1-Heritage");
        addContact("RVS", "Dr. Andrew Mason", "ED1-UofA");

        addContact("SPARK", "Kathy Littlewood", "ED1-UofA");
        addContact("SPARK", "Sheri Cholin", "ED1-UofA");

        addContact("TCKS", "Sharon Gulewich", "CL1-Sunridge");
        addContact("TCKS", "Sue Szigety", "ED1-UofA");
        addContact("VAS", "Dawn Opgenorth", "ED1-UofA");
    }
}
