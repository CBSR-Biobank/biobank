package edu.ualberta.med.biobank.common.wrappers;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.wrappers.base.OriginInfoBaseWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class OriginInfoWrapper extends OriginInfoBaseWrapper {

    public OriginInfoWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public List<SpecimenWrapper> getSpecimenCollection() {
        return getSpecimenCollection(false);
    }

    public Collection<PatientWrapper> getPatientCollection() {
        Collection<SpecimenWrapper> specimens = getSpecimenCollection();
        Set<PatientWrapper> patients = new HashSet<PatientWrapper>();

        for (SpecimenWrapper specimen : specimens) {
            PatientWrapper patient = specimen.getCollectionEvent().getPatient();

            if (!patients.contains(patient)) {
                patients.add(patient);
            }
        }

        return patients;
    }
}
