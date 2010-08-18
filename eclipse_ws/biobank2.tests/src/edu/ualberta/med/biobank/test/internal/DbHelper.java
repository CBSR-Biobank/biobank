package edu.ualberta.med.biobank.test.internal;

import edu.ualberta.med.biobank.common.wrappers.ClinicShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.junit.Assert;

public class DbHelper {

    protected static WritableApplicationService appService;

    protected static Random r = new Random();

    public static void setAppService(WritableApplicationService appService) {
        Assert.assertNotNull("appService is null", appService);
        DbHelper.appService = appService;
    }

    public static <T> T chooseRandomlyInList(List<T> list) {
        if (list.size() == 1) {
            return list.get(0);
        }
        if (list.size() > 1) {
            int pos = r.nextInt(list.size());
            return list.get(pos);
        }
        return null;
    }

    public static void deleteContainers(Collection<ContainerWrapper> containers)
        throws Exception {
        Assert.assertNotNull("appService is null", appService);
        if ((containers == null) || (containers.size() == 0))
            return;

        for (ContainerWrapper container : containers) {
            container.reload();
            if (container.hasChildren()) {
                deleteContainers(container.getChildren().values());
            }
            if (container.hasAliquots()) {
                deleteFromList(container.getAliquots().values());
            }
            container.reload();
            container.delete();
        }
    }

    public static void deleteCreatedStudies() throws Exception {
        Assert.assertNotNull("appService is null", appService);
        for (StudyWrapper study : StudyWrapper.getAllStudies(appService)) {
            deletePatients(study.getPatientCollection());
            deleteFromList(study.getSampleStorageCollection());
            study.reload();
            study.delete();
        }
    }

    public static void deletePatients(List<PatientWrapper> patients)
        throws Exception {
        Assert.assertNotNull("appService is null", appService);
        if (patients == null)
            return;

        // visites liees au ship avec patient de la visit non lie au shipment
        for (PatientWrapper patient : patients) {
            deletePatientVisits(patient.getPatientVisitCollection());
            patient.reload();
            for (ClinicShipmentWrapper ship : patient.getShipmentCollection()) {
                ship.reload();
                ship.removePatients(Arrays.asList(patient));
                if (ship.getPatientCollection().size() == 0) {
                    ship.delete();
                } else {
                    ship.persist();
                }
            }
            patient.reload();
            patient.delete();
        }
    }

    public static void deletePatientVisits(List<PatientVisitWrapper> visits)
        throws Exception {
        Assert.assertNotNull("appService is null", appService);
        if (visits == null)
            return;

        for (PatientVisitWrapper visit : visits) {
            deleteFromList(visit.getAliquotCollection());
            visit.reload();
            visit.delete();
        }
    }

    public static void deleteClinics(List<ClinicWrapper> clinics)
        throws Exception {
        Assert.assertNotNull("appService is null", appService);
        for (ClinicWrapper clinic : clinics) {
            clinic.reload();
            deleteFromList(clinic.getShipmentCollection());
            clinic.reload();
            clinic.delete();
        }
    }

    public static void deleteFromList(Collection<? extends ModelWrapper<?>> list)
        throws Exception {
        if (list == null)
            return;

        for (ModelWrapper<?> object : list) {
            object.reload();
            object.delete();
        }
    }

}
