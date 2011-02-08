package edu.ualberta.med.biobank.test.internal;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.test.wrappers.TestCommon;

public class ProcessingEventHelper extends DbHelper {
    private static ActivityStatusWrapper activeActivityStatus = null;

    /**
     * Creates a new patient visit wrapper. It is not saved to the database.
     * 
     * @param patient The patient that the patient visit belongs to.
     * @param shipment The shipment that the samples where received in.
     * @param dateProcessed The date the aliquot was processed.
     * @return A new patient visit wrapper.
     */
    public static ProcessingEventWrapper newProcessingEvent(
        CenterWrapper center, PatientWrapper patient, Date dateProcessed,
        Date dateDrawn) throws Exception {
        ProcessingEventWrapper pv = new ProcessingEventWrapper(appService);
        pv.setCenter(center);
        pv.setPatient(patient);
        pv.setDateProcessed(dateProcessed);
        pv.setDateDrawn(dateDrawn);
        pv.setActivityStatus(getActiveActivityStatus());

        return pv;
    }

    private static ActivityStatusWrapper getActiveActivityStatus()
        throws Exception {
        if (activeActivityStatus == null) {
            activeActivityStatus = ActivityStatusWrapper.getActivityStatus(
                appService, ActivityStatusWrapper.ACTIVE_STATUS_STRING);
        }

        return activeActivityStatus;
    }

    /**
     * Adds a new patient visit to the database.
     * 
     * @param patient The patient that the patient visit belongs to.
     * @param shipment The shipment that the samples where received in.
     * @param dateProcessed The date the aliquot was processed.
     * @return A new patient visit wrapper.
     * @throws Exception if the object could not be saved to the database.
     */
    public static ProcessingEventWrapper addProcessingEvent(
        CenterWrapper center, PatientWrapper patient, Date dateProcessed,
        Date dateDrawn) throws Exception {
        ProcessingEventWrapper pv = newProcessingEvent(center, patient,
            dateProcessed, dateDrawn);
        pv.persist();
        return pv;
    }

    /**
     * Adds a new patient visit to the database.
     * 
     * @param patient The patient that the patient visit belongs to.
     * @param shipment The shipment associated with the visit.
     * 
     * @return A new patient visit wrapper.
     * 
     * @throws Exception if the object could not be saved to the database.
     */
    public static List<ProcessingEventWrapper> addProcessingEvents(
        CenterWrapper center, PatientWrapper patient, int minimumNumber,
        int maxNumber) throws ParseException, Exception {
        int count = r.nextInt(maxNumber - minimumNumber + 1) + minimumNumber;
        List<ProcessingEventWrapper> visits = new ArrayList<ProcessingEventWrapper>();
        for (int i = 0; i < count; i++) {
            visits.add(addProcessingEvent(center, patient,
                TestCommon.getUniqueDate(r), TestCommon.getUniqueDate(r)));
        }
        return visits;
    }

    public static List<ProcessingEventWrapper> addProcessingEvents(
        CenterWrapper center, PatientWrapper patient, int minimumNumber)
        throws ParseException, Exception {
        return addProcessingEvents(center, patient, minimumNumber, 15);
    }

    public static List<ProcessingEventWrapper> addProcessingEvents(
        CenterWrapper center, PatientWrapper patient) throws ParseException,
        Exception {
        return addProcessingEvents(center, patient, 1);

    }
}
