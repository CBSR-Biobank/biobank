package edu.ualberta.med.biobank.test.action.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.action.IActionExecutor;
import edu.ualberta.med.biobank.common.action.clinic.ClinicGetInfoAction;
import edu.ualberta.med.biobank.common.action.clinic.ClinicGetInfoAction.ClinicInfo;
import edu.ualberta.med.biobank.common.action.container.ContainerSaveAction;
import edu.ualberta.med.biobank.common.action.containerType.ContainerTypeSaveAction;
import edu.ualberta.med.biobank.common.action.info.SiteInfo;
import edu.ualberta.med.biobank.common.action.info.StudyCountInfo;
import edu.ualberta.med.biobank.common.action.patient.PatientSaveAction;
import edu.ualberta.med.biobank.common.action.site.SiteSaveAction;
import edu.ualberta.med.biobank.common.action.study.StudySaveAction;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.test.Utils;

public class SiteHelper extends Helper {

    public static SiteSaveAction getSaveAction(String name,
        String nameShort, ActivityStatus activityStatus) {
        Address address = new Address();
        String city = name + "_city";
        if (city.length() > 50) {
            city = city.substring(city.length() - 50);
        }
        address.setCity(city);

        SiteSaveAction siteSaveAction = new SiteSaveAction();
        siteSaveAction.setName(name);
        siteSaveAction.setNameShort(nameShort);
        siteSaveAction.setActivityStatus(activityStatus);
        siteSaveAction.setAddress(address);
        siteSaveAction.setStudyIds(new HashSet<Integer>());

        return siteSaveAction;
    }

    public static Integer createSite(IActionExecutor actionExecutor,
        String name, String city, ActivityStatus activityStatus,
        Set<Integer> studyIds) {

        Address address = new Address();
        address.setCity(city);

        SiteSaveAction saveSite = new SiteSaveAction();
        saveSite.setName(name);
        saveSite.setNameShort(name);
        saveSite.setAddress(address);
        saveSite.setActivityStatus(activityStatus);
        saveSite.setStudyIds(studyIds);

        return actionExecutor.exec(saveSite).getId();
    }

    public static List<Integer> createSites(
        IActionExecutor actionExecutor,
        String name, ActivityStatus activityStatus, int numToCreate) {
        List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < numToCreate; ++i) {
            result.add(createSite(actionExecutor, name,
                Utils.getRandomString(15),
                activityStatus, new HashSet<Integer>()));
        }
        return result;
    }

    public static SiteSaveAction getSaveAction(SiteInfo siteInfo) {
        SiteSaveAction siteSaveAction = new SiteSaveAction();

        siteSaveAction.setId(siteInfo.getSite().getId());
        siteSaveAction.setName(siteInfo.getSite().getName());
        siteSaveAction.setNameShort(siteInfo.getSite().getNameShort());
        siteSaveAction
            .setActivityStatus(siteInfo.getSite().getActivityStatus());
        siteSaveAction.setAddress(siteInfo.getSite().getAddress());

        Set<Integer> ids = new HashSet<Integer>();
        for (StudyCountInfo infos : siteInfo.getStudyCountInfos()) {
            ids.add(infos.getStudy().getId());
        }
        siteSaveAction.setStudyIds(ids);

        return siteSaveAction;
    }

    /*
     * Creates a clinic, study and site. Clinic is linked to study via contact.
     * Study is linked to Site.
     */
    public static class Provisioning {
        public Integer siteId;
        public Integer studyId;
        public Integer clinicId;
        public List<Integer> patientIds;
        public List<Integer> containerTypeIds;

        public Provisioning(IActionExecutor actionExecutor, String basename) {
            patientIds = new ArrayList<Integer>();
            containerTypeIds = new ArrayList<Integer>();

            clinicId = ClinicHelper.createClinicWithContacts(actionExecutor,
                basename + "_clinic", 1);
            ClinicInfo clinicInfo =
                actionExecutor.exec(new ClinicGetInfoAction(clinicId));
            StudySaveAction studySaveAction =
                StudyHelper.getSaveAction(basename + "_study", basename
                    + "_study",
                    ActivityStatus.ACTIVE);
            HashSet<Integer> ids = new HashSet<Integer>();
            ids.add(clinicInfo.contacts.get(0).getId());
            studySaveAction.setContactIds(ids);
            studyId = actionExecutor.exec(studySaveAction).getId();

            SiteSaveAction siteSaveAction =
                SiteHelper.getSaveAction(basename + "_site",
                    basename + "_site",
                    ActivityStatus.ACTIVE);
            ids = new HashSet<Integer>();
            ids.add(studyId);
            siteSaveAction.setStudyIds(ids);
            siteId = actionExecutor.exec(siteSaveAction).getId();

            PatientSaveAction patientSaveAction =
                new PatientSaveAction(null, studyId,
                    basename + "_patient1", Utils.getRandomDate(), null);
            patientIds.add(actionExecutor.exec(patientSaveAction)
                .getId());
        }

        public Integer addContainerType(IActionExecutor executor,
            String name, Integer containerLabelingSchemeId, double defaultTemp) {
            ContainerTypeSaveAction ctSaveAction =
                ContainerTypeHelper.getSaveAction(name, name, siteId,
                    true, 3, 10,
                    containerLabelingSchemeId, defaultTemp);
            Integer containerTypeId = executor.exec(ctSaveAction).getId();
            containerTypeIds.add(containerTypeId);
            return containerTypeId;
        }

        public Integer addContainer(IActionExecutor executor,
            Integer containerTypeId, String label) {
            ContainerSaveAction containerSaveAction = new ContainerSaveAction();
            containerSaveAction.setActivityStatus(ActivityStatus.ACTIVE);
            containerSaveAction.setBarcode(Utils.getRandomString(5, 10));
            containerSaveAction.setLabel("01");
            containerSaveAction.setSiteId(siteId);
            containerSaveAction.setTypeId(containerTypeId);
            containerSaveAction.setLabel(label);
            return executor.exec(containerSaveAction).getId();
        }
    }
}
