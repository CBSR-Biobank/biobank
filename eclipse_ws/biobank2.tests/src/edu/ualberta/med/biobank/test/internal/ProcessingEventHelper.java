package edu.ualberta.med.biobank.test.internal;

import java.util.Date;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.test.Utils;

public class ProcessingEventHelper extends DbHelper {

    /**
     * Creates a new patient pevent wrapper. It is not saved to the database.
     * 
     * @param patient The patient that the patient pevent belongs to.
     * @param createdAt The date the aliquot was processed.
     * @return A new patient pevent wrapper.
     */
    public static ProcessingEventWrapper newProcessingEvent(
        CenterWrapper<?> center, PatientWrapper patient, Date createdAt)
        throws Exception {
        ProcessingEventWrapper pevent = new ProcessingEventWrapper(appService);
        pevent.setWorksheet(Utils.getRandomString(20));
        pevent.setCenter(center);
        pevent.setCreatedAt(createdAt);
        pevent.setActivityStatus(ActivityStatusWrapper
            .getActiveActivityStatus(appService));
        return pevent;
    }

    /**
     * Adds a new patient pevent to the database.
     * 
     * @param patient The patient that the patient pevent belongs to.
     * @param dateProcessed The date the aliquot was processed.
     * @return A new patient pevent wrapper.
     * @throws Exception if the object could not be saved to the database.
     */
    public static ProcessingEventWrapper addProcessingEvent(
        CenterWrapper<?> center, PatientWrapper patient, Date dateProcessed)
        throws Exception {
        ProcessingEventWrapper pevent = newProcessingEvent(center, patient,
            dateProcessed);
        pevent.persist();
        return pevent;
    }

}
