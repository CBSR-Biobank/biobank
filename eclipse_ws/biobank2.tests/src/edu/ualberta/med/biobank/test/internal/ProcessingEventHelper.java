package edu.ualberta.med.biobank.test.internal;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
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
        PatientWrapper patient, CollectionEventWrapper shipment,
        Date dateProcessed, Date dateDrawn) throws Exception {
        ProcessingEventWrapper pv = new ProcessingEventWrapper(appService);
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
        PatientWrapper patient, CollectionEventWrapper shipment,
        Date dateProcessed, Date dateDrawn) throws Exception {
        ProcessingEventWrapper pv = newProcessingEvent(patient, shipment,
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
        PatientWrapper patient, CollectionEventWrapper shipment,
        int minimumNumber, int maxNumber) throws ParseException, Exception {
        int count = r.nextInt(maxNumber - minimumNumber + 1) + minimumNumber;
        List<ProcessingEventWrapper> visits = new ArrayList<ProcessingEventWrapper>();
        for (int i = 0; i < count; i++) {
            visits.add(addProcessingEvent(patient, shipment,
                TestCommon.getUniqueDate(r), TestCommon.getUniqueDate(r)));
        }
        return visits;
    }

    public static List<ProcessingEventWrapper> addProcessingEvents(
        PatientWrapper patient, CollectionEventWrapper shipment,
        int minimumNumber) throws ParseException, Exception {
        return addProcessingEvents(patient, shipment, minimumNumber, 15);
    }

    public static List<ProcessingEventWrapper> addProcessingEvents(
        PatientWrapper patient, CollectionEventWrapper shipment)
        throws ParseException, Exception {
        return addProcessingEvents(patient, shipment, 1);

    }
}
