package edu.ualberta.med.biobank.test.internal;

import java.util.Random;

import edu.ualberta.med.biobank.common.cbsr.CbsrSite;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.test.wrappers.TestCommon;

public class AliquotHelper extends DbHelper {

    public static AliquotWrapper newAliquot(SampleTypeWrapper sampleType) {
        AliquotWrapper aliquot = new AliquotWrapper(appService);
        aliquot.setSampleType(sampleType);
        aliquot.setInventoryId(TestCommon.getNewInventoryId(new Random()));
        return aliquot;
    }

    public static AliquotWrapper newAliquot(SampleTypeWrapper sampleType,
        ContainerWrapper container, PatientVisitWrapper pv, Integer row,
        Integer col) throws Exception {
        AliquotWrapper aliquot = new AliquotWrapper(appService);
        aliquot.setSampleType(sampleType);
        aliquot.setInventoryId(TestCommon.getNewInventoryId(new Random()));
        if (container != null) {
            aliquot.setParent(container);
        }
        aliquot.setPatientVisit(pv);
        if ((row != null) && (col != null)) {
            aliquot.setPosition(row, col);
        }
        aliquot.setActivityStatus(CbsrSite.getActivityStatus("Active"));
        return aliquot;
    }

    public static AliquotWrapper addAliquot(SampleTypeWrapper sampleType,
        ContainerWrapper container, PatientVisitWrapper pv, Integer row,
        Integer col) throws Exception {
        AliquotWrapper aliquot = newAliquot(sampleType, container, pv, row, col);
        aliquot.persist();
        return aliquot;
    }

}
