package edu.ualberta.med.biobank.common.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.common.config.calgary.CalgarySite;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleStorageWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudySourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class ConfigStudies {

    protected SiteWrapper site;

    protected Map<String, StudyWrapper> studiesMap = null;

    protected Map<String, SourceVesselWrapper> sourceVesselMap = null;

    protected Map<String, ClinicWrapper> clinicsMap;

    protected ConfigStudies(SiteWrapper site) throws Exception {
        if (site == null) {
            throw new Exception("site is null");
        }
        this.site = site;
        clinicsMap = new HashMap<String, ClinicWrapper>();
        for (ClinicWrapper clinic : ClinicWrapper.getAllClinics(site
            .getAppService())) {
            clinicsMap.put(clinic.getNameShort(), clinic);
        }
        if (clinicsMap.size() < 1) {
            throw new Exception("no clinics present in site "
                + site.getNameShort());
        }
    }

    protected StudyWrapper addStudy(SiteWrapper site, String name,
        String nameShort, String activityStatusName, String comment)
        throws Exception {
        StudyWrapper study = new StudyWrapper(site.getAppService());
        study.setSite(site);
        study.setName(name);
        study.setNameShort(nameShort);
        study.setActivityStatus(CalgarySite
            .getActivityStatus(activityStatusName));
        study.setComment(comment);
        study.persist();
        study.reload();
        studiesMap.put(nameShort, study);
        return study;
    }

    public StudyWrapper getStudy(String name) throws Exception {
        StudyWrapper study = studiesMap.get(name);
        if (study == null) {
            throw new Exception("study with name \"" + name
                + "\" does not exist");
        }
        return study;
    }

    public List<String> getStudyNames() throws Exception {
        if (studiesMap == null) {
            throw new Exception("contacts have not been added");
        }
        return new ArrayList<String>(studiesMap.keySet());
    }

    protected void addStudySourceVessel(String studyNameShort, String name)
        throws Exception {
        StudyWrapper study = getStudy(studyNameShort);
        SourceVesselWrapper ss = sourceVesselMap.get(name);
        if (ss == null) {
            throw new Exception("invalid source vessel name: " + name);
        }
        StudySourceVesselWrapper studySourceVessel = new StudySourceVesselWrapper(
            ss.getAppService());
        studySourceVessel.setStudy(study);
        studySourceVessel.setSourceVessel(ss);
        study.addStudySourceVessels(Arrays.asList(studySourceVessel));
        study.persist();
        study.reload();
    }

    protected void addSampleStorage(String studyNameShort,
        String sampleTypeName, String quantity, String volume,
        String activityStatusName) throws Exception {
        StudyWrapper study = getStudy(studyNameShort);

        SampleStorageWrapper ss = new SampleStorageWrapper(study
            .getAppService());
        ss.setSampleType(CalgarySite.getSampleType(sampleTypeName));
        if (quantity != null) {
            ss.setQuantity(Integer.valueOf(quantity));
        } else {
            ss.setQuantity(null);
        }

        if (volume != null) {
            ss.setVolume(Double.valueOf(volume));
        } else {
            ss.setVolume(null);
        }

        ss.setActivityStatus(CalgarySite.getActivityStatus(activityStatusName));

        study.addSampleStorage(Arrays.asList(ss));
        study.persist();
        study.reload();

    }

    protected void addPvAttr(SiteWrapper site, String studyNameShort,
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

    protected void addPvAttr(SiteWrapper site, String studyNameShort,
        String label, String type) throws Exception {
        addPvAttr(site, studyNameShort, label, type, null);
    }

    protected void addContact(String studyNameShort, String contactName,
        String clinicNameShort) throws Exception {
        ContactWrapper contact = getClinic(clinicNameShort).getContact(
            contactName);
        if (contact == null) {
            throw new Exception("clinic " + clinicNameShort
                + " does not have a contact with name " + contactName);
        }
        StudyWrapper study = getStudy(studyNameShort);

        study.addContacts(Arrays.asList(contact));
        study.persist();
        study.reload();
    }

    protected ClinicWrapper getClinic(String nameShort) throws Exception {
        ClinicWrapper clinic = clinicsMap.get(nameShort);
        if (clinic == null) {
            throw new Exception("clinic with name \"" + nameShort
                + "\" does not exist");
        }
        return clinic;
    }

}
