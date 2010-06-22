package edu.ualberta.med.biobank.common.config.calgary;

import java.util.HashMap;

import edu.ualberta.med.biobank.common.config.ConfigStudies;
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
public class CalgaryStudies extends ConfigStudies {

    public static void createStudies(SiteWrapper site) throws Exception {
        studiesMap = new HashMap<String, StudyWrapper>();

        sourceVesselMap = new HashMap<String, SourceVesselWrapper>();
        for (SourceVesselWrapper sourceVessel : SourceVesselWrapper
            .getAllSourceVessels(site.getAppService())) {
            sourceVesselMap.put(sourceVessel.getName(), sourceVessel);
        }

        addStudy(site, "Heart failure Etiology and Analysis Research Team",
            "HEART", ActivityStatusWrapper.ACTIVE_STATUS_STRING, null);

        addStudySourceVessel("HEART", "10ml green top Lithium Heparin tube");
        addStudySourceVessel("HEART", "10mL lavender top EDTA tube");
        addStudySourceVessel("HEART", "10ml orange top PAXgene tube");
        addStudySourceVessel("HEART", "5mL gold top serum tube");
        addStudySourceVessel("HEART", "6mL lavender top EDTA tube");
        addStudySourceVessel("HEART", "8.5ml P100 orange top tube");
        addStudySourceVessel("HEART", "urine cup");

        addSampleStorage("HEART", "DNA E 1000", "5", "1", "Active");
        addSampleStorage("HEART", "DNA L 1000", "5", "1", "Active");
        addSampleStorage("HEART", "LH PFP 200", "10", "0.2", "Active");
        addSampleStorage("HEART", "LH PFP 500", "5", "0.5", "Active");
        addSampleStorage("HEART", "P100 500", "8", "0.5", "Active");
        addSampleStorage("HEART", "PlasmaE200", "10", "0.2", "Active");
        addSampleStorage("HEART", "PlasmaE500", "5", "0.5", "Active");
        addSampleStorage("HEART", "PlasmaE800", "3", "0.8", "Active");
        addSampleStorage("HEART", "PlasmaL200", "10", "0.2", "Active");
        addSampleStorage("HEART", "PlasmaL500", "5", "0.5", "Active");
        addSampleStorage("HEART", "SerumG500", "5", "0.5", "Active");
        addSampleStorage("HEART", "UrineC900", "6", "0.9", "Active");
        addSampleStorage("HEART", "UrineSA900", "12", "0.9", "Active");

        addContact("HEART", "Morna Brown", "CL1-Foothills");
    }

}
