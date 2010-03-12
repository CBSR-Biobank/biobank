package edu.ualberta.med.biobank.test.internal;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public class DbHelper {

    protected static WritableApplicationService appService;

    protected static Random r = new Random();

    public static void setAppService(WritableApplicationService appService) {
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
        if ((containers == null) || (containers.size() == 0))
            return;

        for (ContainerWrapper container : containers) {
            container.reload();
            if (container.hasChildren()) {
                deleteContainers(container.getChildren().values());
            }
            if (container.hasSamples()) {
                deleteFromList(container.getAliquots().values());
            }
            container.reload();
            container.delete();
        }
    }

    public static void deleteStudies(List<StudyWrapper> studies)
        throws Exception {
        if (studies == null)
            return;

        for (StudyWrapper study : studies) {
            deletePatients(study.getPatientCollection());
            study.reload();
            study.delete();
        }
    }

    public static void deletePatients(List<PatientWrapper> patients)
        throws Exception {
        if (patients == null)
            return;

        for (PatientWrapper patient : patients) {
            deletePatientVisits(patient.getPatientVisitCollection());
            patient.reload();
            patient.delete();
        }
    }

    public static void deletePatientVisits(List<PatientVisitWrapper> visits)
        throws Exception {
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
