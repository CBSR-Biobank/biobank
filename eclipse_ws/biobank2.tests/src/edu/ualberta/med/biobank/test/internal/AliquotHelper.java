package edu.ualberta.med.biobank.test.internal;

import java.util.Random;

import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.test.wrappers.TestCommon;

public class AliquotHelper extends DbHelper {

    public static AliquotWrapper newSample(SampleTypeWrapper sampleType) {
        AliquotWrapper sample = new AliquotWrapper(appService);
        sample.setSampleType(sampleType);
        sample.setInventoryId(TestCommon.getNewInventoryId(new Random()));
        return sample;
    }

    public static AliquotWrapper newAliquot(SampleTypeWrapper sampleType,
        ContainerWrapper container, PatientVisitWrapper pv, Integer row,
        Integer col) {
        AliquotWrapper sample = new AliquotWrapper(appService);
        sample.setSampleType(sampleType);
        sample.setInventoryId(TestCommon.getNewInventoryId(new Random()));
        if (container != null) {
            sample.setParent(container);
        }
        sample.setPatientVisit(pv);
        if ((row != null) && (col != null)) {
            sample.setPosition(row, col);
        }
        return sample;
    }

    public static AliquotWrapper addAliquot(SampleTypeWrapper sampleType,
        ContainerWrapper container, PatientVisitWrapper pv, Integer row,
        Integer col) throws Exception {
        AliquotWrapper sample = newAliquot(sampleType, container, pv, row, col);
        sample.persist();
        return sample;
    }

}
