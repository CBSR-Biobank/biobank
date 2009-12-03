package test.ualberta.med.biobank.internal;

import test.ualberta.med.biobank.Utils;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleWrapper;

public class SampleHelper extends DbHelper {

    public static SampleWrapper newSample(SampleTypeWrapper sampleType,
        ContainerWrapper container, PatientVisitWrapper pv, Integer row,
        Integer col) {
        SampleWrapper sample = new SampleWrapper(appService);
        sample.setSampleType(sampleType);
        sample.setInventoryId(Utils.getRandomString(5));
        sample.setParent(container);
        sample.setPatientVisit(pv);
        sample.setPosition(row, col);
        return sample;
    }

    public static SampleWrapper addSample(SampleTypeWrapper sampleType,
        ContainerWrapper container, PatientVisitWrapper pv, Integer row,
        Integer col) throws Exception {
        SampleWrapper sample = newSample(sampleType, container, pv, row, col);
        sample.persist();
        return sample;
    }

}
