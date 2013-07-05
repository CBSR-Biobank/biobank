package edu.ualberta.med.biobank.test.action.helper;

import java.util.Date;

import edu.ualberta.med.biobank.action.IActionExecutor;
import edu.ualberta.med.biobank.common.action.patient.PatientSaveAction;
import edu.ualberta.med.biobank.test.Utils;

public class PatientHelper extends Helper {

    public static Integer createPatient(IActionExecutor actionExecutor,
        String s, Integer studyId) {
        String pnumber = s + r.nextInt();
        Date date = Utils.getRandomDate();
        Integer patientId = actionExecutor.exec(new PatientSaveAction(null,
            studyId, pnumber, date, null)).getId();
        return patientId;
    }
}
