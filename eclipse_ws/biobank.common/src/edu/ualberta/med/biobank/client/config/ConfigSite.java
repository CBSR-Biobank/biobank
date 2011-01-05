package edu.ualberta.med.biobank.client.config;

import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigSite {

    public static WritableApplicationService appService;

    public static SiteWrapper site = null;

    private static Map<String, SampleTypeWrapper> sampleTypeMap;

    public static SiteWrapper getSite() {
        return site;
    }

    public static void getSampleTypeMap(WritableApplicationService appService)
        throws Exception {
        if (sampleTypeMap != null)
            return;

        List<SampleTypeWrapper> allSampleTypes = SampleTypeWrapper
            .getAllSampleTypes(appService, true);
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

    protected static void siteDeleteSubObjects(SiteWrapper site)
        throws Exception {
        List<StudyWrapper> studies = site.getStudyCollection(false);
        if (studies != null) {
            for (StudyWrapper study : studies) {
                studyDeleteSubObjects(study);
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
        for (ShipmentWrapper ship : patient.getShipmentCollection(null)) {
            ship.removePatients(Arrays.asList(patient));
            if (ship.getPatientCollection().size() == 0) {
                ship.delete();
            } else {
                ship.persist();
            }
        }
        patient.reload();
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
