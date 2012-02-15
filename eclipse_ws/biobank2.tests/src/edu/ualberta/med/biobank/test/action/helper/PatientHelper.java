package edu.ualberta.med.biobank.test.action.helper;

import java.util.Date;

import edu.ualberta.med.biobank.common.action.patient.PatientSaveAction;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.IActionExecutor;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class PatientHelper extends Helper {

    public static Integer createPatient(IActionExecutor actionExecutor,
        String s, Integer studyId) throws ApplicationException {
        String pnumber = s + r.nextInt();
        Date date = Utils.getRandomDate();
        Integer patientId = actionExecutor.exec(new PatientSaveAction(null,
            studyId, pnumber, date, null)).getId();
        return patientId;
    }
}
