package edu.ualberta.med.biobank.test.action.helper;

import java.util.Date;

import edu.ualberta.med.biobank.common.action.patient.PatientSaveAction;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.test.Utils;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class PatientHelper extends Helper {

    public static Integer createPatient(BiobankApplicationService appService,
        String s, Integer studyId) throws ApplicationException {
        String pnumber = s + r.nextInt();
        Date date = Utils.getRandomDate();
        Integer patientId = appService.doAction(new PatientSaveAction(null,
            studyId, pnumber, date));
        return patientId;
    }
}
