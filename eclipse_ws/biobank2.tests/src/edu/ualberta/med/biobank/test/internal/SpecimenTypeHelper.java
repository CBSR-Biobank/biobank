package edu.ualberta.med.biobank.test.internal;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;

public class SpecimenTypeHelper extends DbHelper {

    public static List<SpecimenTypeWrapper> createdSampleTypes = new ArrayList<SpecimenTypeWrapper>();

    public static SpecimenTypeWrapper newSampleType(String name) throws Exception {
        SpecimenTypeWrapper type = new SpecimenTypeWrapper(appService);
        type.setName(name);
        type.setNameShort(name);
        return type;
    }

    public static SpecimenTypeWrapper addSampleType(String name,
        boolean addToCreatedList) throws Exception {
        SpecimenTypeWrapper type = newSampleType(name);
        type.persist();
        if (addToCreatedList) {
            createdSampleTypes.add(type);
        }
        return type;
    }

    public static SpecimenTypeWrapper addSampleType(String name) throws Exception {
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
        for (SpecimenTypeWrapper type : createdSampleTypes) {
            type.reload();
            type.delete();
        }
        createdSampleTypes.clear();
    }

    public static void removeFromCreated(SpecimenTypeWrapper type) {
        createdSampleTypes.remove(type);
    }

}
