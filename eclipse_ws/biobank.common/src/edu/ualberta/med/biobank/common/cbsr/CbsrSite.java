package edu.ualberta.med.biobank.common.cbsr;

import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.PvSourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CbsrSite {

    public static WritableApplicationService appService;

    public static SiteWrapper cbsrSite = null;

    private static Map<String, SampleTypeWrapper> sampleTypeMap;

    public static void createConfiguration(WritableApplicationService appServ)
        throws Exception {
        appService = appServ;
        addSite(appService);
        CbsrClinics.createClinics(cbsrSite);
        CbsrStudies.createStudies(cbsrSite);
        CbsrContainerTypes.createContainerTypes(cbsrSite);
        CbsrContainers.createContainers(cbsrSite);
    }

    public static SiteWrapper addSite(WritableApplicationService appService)
        throws Exception {
        getSampleTypeMap(appService);
        cbsrSite = new SiteWrapper(appService);
        cbsrSite.setName("Canadian BioSample Repository");
        cbsrSite.setNameShort("CBSR");
        cbsrSite.setActivityStatus(getActiveActivityStatus());
        cbsrSite.setStreet1("471 Medical Sciences Building");
        cbsrSite.setStreet2("University of Alberta");
        cbsrSite.setCity("Edmonton");
        cbsrSite.setProvince("Alberta");
        cbsrSite.setPostalCode("T6G2H7");
        cbsrSite.persist();
        cbsrSite.reload();
        return cbsrSite;
    }

    public static SiteWrapper getSite() {
        return cbsrSite;
    }

    public static void getSampleTypeMap(WritableApplicationService appService)
        throws Exception {
        if (sampleTypeMap != null)
            return;

        List<SampleTypeWrapper> allSampleTypes = SampleTypeWrapper
            .getGlobalSampleTypes(appService, true);
        if ((allSampleTypes == null) || (allSampleTypes.size() == 0)) {
            throw new Exception("no global sample types found in the database");
        }

        sampleTypeMap = new HashMap<String, SampleTypeWrapper>();
        for (SampleTypeWrapper sampleType : allSampleTypes) {
            sampleTypeMap.put(sampleType.getNameShort(), sampleType);
        }
    }

    public static SampleTypeWrapper getSampleType(String nameShort)
        throws Exception {
        SampleTypeWrapper sampleType = sampleTypeMap.get(nameShort);
        if (sampleType == null) {
            throw new Exception("sample type \"" + nameShort
                + "\" not found in the database");
        }
        return sampleType;
    }

    public static ActivityStatusWrapper getActivityStatus(String name)
        throws Exception {
        return ActivityStatusWrapper.getActivityStatus(appService, name);
    }

    public static ActivityStatusWrapper getActiveActivityStatus()
        throws Exception {
        return ActivityStatusWrapper.getActivityStatus(appService, "Active");
    }

    public static void deleteConfiguration(WritableApplicationService appServ)
        throws Exception {
        appService = appServ;

        List<SiteWrapper> sites = SiteWrapper.getSites(appService);
        if (sites == null)
            return;
        for (SiteWrapper site : sites) {
            if (site.getName().equals("Canadian BioSample Repository")) {
                siteDeleteSubObjects(site);
            }
        }
    }

    private static void siteDeleteSubObjects(SiteWrapper site) throws Exception {
        List<StudyWrapper> studies = site.getStudyCollection(false);
        if (studies != null) {
            for (StudyWrapper study : studies) {
                studyDeleteSubObjects(study);
            }
            site.reload();
        }
        List<ClinicWrapper> clinics = site.getClinicCollection(false);
        if (clinics != null) {
            for (ClinicWrapper clinic : clinics) {
                clinicDeleteSubObjects(clinic);
            }
            site.reload();
        }
        List<ContainerWrapper> containers = site.getTopContainerCollection();
        if (containers != null) {
            for (ContainerWrapper container : containers) {
                containerDeleteSubObjects(container);
            }
            site.reload();
        }
        List<ContainerTypeWrapper> containerTypes = site
            .getContainerTypeCollection();
        if (containerTypes != null) {
            for (ContainerTypeWrapper containerType : containerTypes) {
                containerTypeDeleteSubObjects(containerType);
            }
            site.reload();
        }
        site.delete();
    }

    private static void studyDeleteSubObjects(StudyWrapper study)
        throws Exception {
        List<PatientWrapper> patients = study.getPatientCollection();
        if (patients == null)
            return;
        for (PatientWrapper patient : patients) {
            patientDeleteSubObjects(patient);
        }
        study.reload();
        study.delete();
    }

    private static void patientDeleteSubObjects(PatientWrapper patient)
        throws Exception {
        List<PatientVisitWrapper> visits = patient.getPatientVisitCollection();
        if (visits == null)
            return;
        for (PatientVisitWrapper visit : visits) {
            patientVisitDeleteSubObjects(visit);
        }
        patient.reload();
        patient.delete();
    }

    private static void patientVisitDeleteSubObjects(PatientVisitWrapper visit)
        throws Exception {
        List<PvSourceVesselWrapper> sourceVessels = visit
            .getPvSourceVesselCollection();
        if (sourceVessels != null) {
            for (PvSourceVesselWrapper sourceVessel : sourceVessels) {
                sourceVessel.delete();
            }
        }
        visit.reload();
        List<AliquotWrapper> aliquots = visit.getAliquotCollection();
        if (aliquots != null) {
            for (AliquotWrapper aliquot : aliquots) {
                aliquot.delete();
            }
        }
        visit.reload();
        visit.delete();
    }

    private static void clinicDeleteSubObjects(ClinicWrapper clinic)
        throws Exception {
        List<ContactWrapper> contacts = clinic.getContactCollection();
        if (contacts != null) {
            for (ContactWrapper contact : contacts) {
                contact.delete();
            }
            clinic.reload();
        }
        List<ShipmentWrapper> shipments = clinic.getShipmentCollection();
        if (shipments != null) {
            for (ShipmentWrapper contact : shipments) {
                contact.delete();
            }
            clinic.reload();
        }
        clinic.delete();
    }

    private static void containerDeleteSubObjects(ContainerWrapper container)
        throws Exception {
        Map<RowColPos, AliquotWrapper> samples = container.getAliquots();
        if (samples.size() > 0) {
            // samples should be deleted when patient visits are deleted
            throw new Exception(
                "error with deletetion: containers should have no samples left");
        }

        Map<RowColPos, ContainerWrapper> children = container.getChildren();
        for (ContainerWrapper child : children.values()) {
            containerDeleteSubObjects(child);
        }
        container.reload();
        container.delete();
    }

    private static void containerTypeDeleteSubObjects(
        ContainerTypeWrapper containerType) throws Exception {
        List<ContainerTypeWrapper> children = containerType
            .getChildContainerTypeCollection();
        if (children != null) {
            for (ContainerTypeWrapper child : children) {
                containerTypeDeleteSubObjects(child);
            }
            containerType.reload();
        }
        containerType.delete();
    }

}
