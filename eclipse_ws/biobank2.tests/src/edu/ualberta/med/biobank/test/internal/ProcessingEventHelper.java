package edu.ualberta.med.biobank.test.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.wrappers.TestCommon;

public class ProcessingEventHelper extends DbHelper {

    /**
     * Creates a new patient pevent wrapper. It is not saved to the database.
     * 
     * @param patient The patient that the patient pevent belongs to.
     * @param createdAt The date the specimen was processed.
     * @return A new patient pevent wrapper.
     */
    public static ProcessingEventWrapper newProcessingEvent(
        CenterWrapper<?> center, PatientWrapper patient, Date createdAt)
        throws Exception {
        ProcessingEventWrapper pevent = new ProcessingEventWrapper(appService);
        pevent.setWorksheet(TestCommon.getUniqueWorksheet(r));
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
     * @param dateProcessed The date the specimen was processed.
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

    public static List<ProcessingEventWrapper> addProcessingEvents(
        CenterWrapper<?> center, PatientWrapper patient, Date dateProcessed,
        SpecimenWrapper parentSpc, List<SpecimenTypeWrapper> spcTypes,
        int maxProcEvent, int spcPerProcEvent) throws Exception {
        CollectionEventWrapper cevent = parentSpc.getCollectionEvent();
        List<ProcessingEventWrapper> pevents = new ArrayList<ProcessingEventWrapper>();
        for (int i = 0; i < maxProcEvent; ++i) {
            for (int j = 0; j < spcPerProcEvent; ++j) {
                ProcessingEventWrapper pe = ProcessingEventHelper
                    .addProcessingEvent(center, patient, Utils.getRandomDate());
                SpecimenHelper.addSpecimen(parentSpc,
                    DbHelper.chooseRandomlyInList(spcTypes), pe);
                pevents.add(pe);
            }
        }
        return pevents;
    }

}
