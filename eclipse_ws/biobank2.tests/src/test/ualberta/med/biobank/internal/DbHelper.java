package test.ualberta.med.biobank.internal;

import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.util.List;
import java.util.Random;

public class DbHelper {

    protected static WritableApplicationService appService;

    protected static Random r = new Random();

    public static void setAppService(WritableApplicationService appService) {
        DbHelper.appService = appService;
    }

    public static void deleteContainers(List<ContainerWrapper> containers)
        throws Exception {
        if ((containers == null) || (containers.size() == 0))
            return;

        for (ContainerWrapper container : containers) {
            if (container.hasChildren()) {
                deleteContainers(container.getChildren());
            } else {
                deleteFromList(container.getSamples());
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
            deleteFromList(patient.getPatientVisitCollection());
            patient.reload();
            patient.delete();
        }
    }

    public static void deleteFromList(List<? extends ModelWrapper<?>> list)
        throws Exception {
        if (list == null)
            return;

        for (ModelWrapper<?> object : list) {
            object.reload();
            object.delete();
        }
    }

}
