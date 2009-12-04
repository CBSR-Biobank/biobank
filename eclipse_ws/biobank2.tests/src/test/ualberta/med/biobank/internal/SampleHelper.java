package test.ualberta.med.biobank.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import test.ualberta.med.biobank.Utils;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleWrapper;

public class SampleHelper extends DbHelper {

    public static List<String> usedInventoryIds;

    public static SampleWrapper newSample(SampleTypeWrapper sampleType,
        ContainerWrapper container, PatientVisitWrapper pv, Integer row,
        Integer col) {
        SampleWrapper sample = new SampleWrapper(appService);
        sample.setSampleType(sampleType);
        sample.setInventoryId(getNewInventoryId(r));
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

    public static String getNewInventoryId(Random r) {
        if (usedInventoryIds == null) {
            usedInventoryIds = new ArrayList<String>();
        }

        String inventoryId;
        do {
            inventoryId = Utils.getRandomString(10);
        } while (usedInventoryIds.contains(inventoryId));
        usedInventoryIds.add(inventoryId);
        return inventoryId;
    }
}
