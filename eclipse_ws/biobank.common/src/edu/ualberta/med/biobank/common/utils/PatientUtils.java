package edu.ualberta.med.biobank.common.utils;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class PatientUtils {

    private static final Logger logger = Logger.getLogger(PatientUtils.class
        .getName());

    public static Patient getPatientInSite(
        WritableApplicationService appService, String patientNumber, Site site) {
        HQLCriteria criteria = new HQLCriteria("from "
            + Patient.class.getName() + " where study.site = ? and number = ?",
            Arrays.asList(new Object[] { site, patientNumber }));
        List<Patient> patients;
        try {
            patients = appService.query(criteria);
            if (patients.size() == 1) {
                return patients.get(0);
            }
        } catch (ApplicationException e) {
            logger.error("Problem while queriyng a patient", e);
        }
        return null;
    }
}
