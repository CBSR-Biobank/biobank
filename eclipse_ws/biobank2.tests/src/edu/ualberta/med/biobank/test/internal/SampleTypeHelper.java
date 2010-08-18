package edu.ualberta.med.biobank.test.internal;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;

public class SampleTypeHelper extends DbHelper {

    public static List<SampleTypeWrapper> createdSampleTypes = new ArrayList<SampleTypeWrapper>();

    public static SampleTypeWrapper newSampleType(String name) throws Exception {
        SampleTypeWrapper type = new SampleTypeWrapper(appService);
        type.setName(name);
        type.setNameShort(name);
        return type;
    }

    public static SampleTypeWrapper addSampleType(String name,
        boolean addToCreatedList) throws Exception {
        SampleTypeWrapper type = newSampleType(name);
        type.persist();
        if (addToCreatedList) {
            createdSampleTypes.add(type);
        }
        return type;
    }

    public static SampleTypeWrapper addSampleType(String name) throws Exception {
        return addSampleType(name, true);
    }

    public static int addSampleTypes(String name) throws Exception {
        int nber = r.nextInt(15) + 2;
        for (int i = 0; i < nber; i++) {
            addSampleType(name + i);
        }
        return nber;
    }

    public static void deleteCreatedSampleTypes() throws Exception {
        for (SampleTypeWrapper type : createdSampleTypes) {
            type.reload();
            type.delete();
        }
        createdSampleTypes.clear();
    }

    public static void removeFromCreated(SampleTypeWrapper type) {
        createdSampleTypes.remove(type);
    }

}
