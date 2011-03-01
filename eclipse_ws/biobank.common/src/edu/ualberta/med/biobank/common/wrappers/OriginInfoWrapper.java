package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.base.OriginInfoBaseWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class OriginInfoWrapper extends OriginInfoBaseWrapper {

    public OriginInfoWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public List<SpecimenWrapper> getSpecimenCollection() {
        return getSpecimenCollection(false);
    }

    public List<PatientWrapper> getPatientCollection() {
        List<SpecimenWrapper> specs = getSpecimenCollection();
        List<PatientWrapper> patients = new ArrayList<PatientWrapper>();
        for (SpecimenWrapper spec : specs)
            patients.add(spec.getCollectionEvent().getPatient());
        return patients;
    }

}
