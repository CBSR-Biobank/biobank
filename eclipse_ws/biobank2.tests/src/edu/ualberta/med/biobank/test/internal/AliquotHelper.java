package edu.ualberta.med.biobank.test.internal;

import java.util.Random;

import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.test.wrappers.TestCommon;

public class AliquotHelper extends DbHelper {

    public static SpecimenWrapper newAliquot(SpecimenTypeWrapper sampleType,
        String activityStatus) throws Exception {
        SpecimenWrapper aliquot = new SpecimenWrapper(appService);
        aliquot.setSpecimenType(sampleType);
        aliquot.setInventoryId(TestCommon.getNewInventoryId(new Random()));
        if (activityStatus != null) {
            aliquot.setActivityStatus(ActivityStatusWrapper.getActivityStatus(
                appService, activityStatus));
        }
        return aliquot;
    }

    public static SpecimenWrapper newAliquot(SpecimenTypeWrapper sampleType)
        throws Exception {
        return newAliquot(sampleType,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
    }

    public static SpecimenWrapper newAliquot(SpecimenTypeWrapper sampleType,
        String activityStatus, ContainerWrapper container,
        ProcessingEventWrapper pv, Integer row, Integer col) throws Exception {
        SpecimenWrapper aliquot = newAliquot(sampleType, activityStatus);
        if (container != null) {
            aliquot.setParent(container);
        }
        aliquot.setProcessingEvent(pv);
        if ((row != null) && (col != null)) {
            aliquot.setPosition(new RowColPos(row, col));
        }
        return aliquot;
    }

    public static SpecimenWrapper newAliquot(SpecimenTypeWrapper sampleType,
        ContainerWrapper container, ProcessingEventWrapper pv, Integer row,
        Integer col) throws Exception {
        return newAliquot(sampleType,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, container, pv, row, col);
    }

    public static SpecimenWrapper addAliquot(SpecimenTypeWrapper sampleType,
        String activityStatus, ContainerWrapper container,
        ProcessingEventWrapper pv, Integer row, Integer col) throws Exception {
        SpecimenWrapper aliquot = newAliquot(sampleType, activityStatus,
            container, pv, row, col);
        aliquot.persist();
        return aliquot;
    }

    public static SpecimenWrapper addAliquot(SpecimenTypeWrapper sampleType,
        ContainerWrapper container, ProcessingEventWrapper pv, Integer row,
        Integer col) throws Exception {
        SpecimenWrapper aliquot = addAliquot(sampleType,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, container, pv, row, col);
        if (container != null)
            container.reload();
        return aliquot;
    }
}
