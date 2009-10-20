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

    protected static Random r;

    public static void setAppService(WritableApplicationService appService) {
        DbHelper.appService = appService;
    }

    public static void setRandomc(Random r) {
        DbHelper.r = r;
    }

    public static void removeContainers(List<ContainerWrapper> containers)
        throws Exception {
        for (ContainerWrapper container : containers) {
            if (container.hasChildren()) {
                removeContainers(container.getChildren());
            } else {
                removeFromList(container.getSamples());
            }
            container.reload();
            container.delete();
        }
    }

    public static void removeStudies(List<StudyWrapper> studies)
        throws Exception {
        for (StudyWrapper study : studies) {
            removePatients(study.getPatientCollection());
            study.reload();
            study.delete();
        }
    }

    public static void removePatients(List<PatientWrapper> patients)
        throws Exception {
        for (PatientWrapper patient : patients) {
            removeFromList(patient.getPatientVisitCollection());
            patient.reload();
            patient.delete();
        }
    }

    public static void removeFromList(List<? extends ModelWrapper<?>> list)
        throws Exception {
        if (list != null) {
            for (ModelWrapper<?> object : list) {
                object.reload();
                object.delete();
            }
        }
    }

}
