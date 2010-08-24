package edu.ualberta.med.biobank.test.internal;

import java.util.Random;

import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.test.wrappers.TestCommon;

public class AliquotHelper extends DbHelper {

    public static AliquotWrapper newAliquot(SampleTypeWrapper sampleType,
        String activityStatus) throws Exception {
        AliquotWrapper aliquot = new AliquotWrapper(appService);
        aliquot.setSampleType(sampleType);
        aliquot.setInventoryId(TestCommon.getNewInventoryId(new Random()));
        if (activityStatus != null) {
            aliquot.setActivityStatus(ActivityStatusWrapper.getActivityStatus(
                appService, activityStatus));
        }
        return aliquot;
    }

    public static AliquotWrapper newAliquot(SampleTypeWrapper sampleType)
        throws Exception {
        return newAliquot(sampleType, "Active");
    }

    public static AliquotWrapper newAliquot(SampleTypeWrapper sampleType,
        String activityStatus, ContainerWrapper container,
        PatientVisitWrapper pv, Integer row, Integer col) throws Exception {
        AliquotWrapper aliquot = newAliquot(sampleType, activityStatus);
        if (container != null) {
            aliquot.setParent(container);
        }
        aliquot.setPatientVisit(pv);
        if ((row != null) && (col != null)) {
            aliquot.setPosition(new RowColPos(row, col));
        }
        return aliquot;
    }

    public static AliquotWrapper newAliquot(SampleTypeWrapper sampleType,
        ContainerWrapper container, PatientVisitWrapper pv, Integer row,
        Integer col) throws Exception {
        return newAliquot(sampleType, "Active", container, pv, row, col);
    }

    public static AliquotWrapper addAliquot(SampleTypeWrapper sampleType,
        String activityStatus, ContainerWrapper container,
        PatientVisitWrapper pv, Integer row, Integer col) throws Exception {
        AliquotWrapper aliquot = newAliquot(sampleType, activityStatus,
            container, pv, row, col);
        aliquot.persist();
        return aliquot;
    }

    public static AliquotWrapper addAliquot(SampleTypeWrapper sampleType,
        ContainerWrapper container, PatientVisitWrapper pv, Integer row,
        Integer col) throws Exception {
        AliquotWrapper aliquot = addAliquot(sampleType, "Active", container,
            pv, row, col);
        if (container != null)
            container.reload();
        return aliquot;
    }
}
