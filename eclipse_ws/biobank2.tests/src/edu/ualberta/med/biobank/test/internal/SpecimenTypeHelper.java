package edu.ualberta.med.biobank.test.internal;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;

@Deprecated
public class SpecimenTypeHelper extends DbHelper {

    public static List<SpecimenTypeWrapper> createdSpecimenTypes =
        new ArrayList<SpecimenTypeWrapper>();

    public static SpecimenTypeWrapper newSpecimenType(String name)
        throws Exception {
        SpecimenTypeWrapper type = new SpecimenTypeWrapper(appService);
        type.setName(name);
        type.setNameShort(name);
        return type;
    }

    public static SpecimenTypeWrapper addSpecimenType(String name,
        boolean addToCreatedList) throws Exception {
        SpecimenTypeWrapper type = newSpecimenType(name);
        type.persist();
        if (addToCreatedList) {
            createdSpecimenTypes.add(type);
        }
        return type;
    }

    public static SpecimenTypeWrapper addSpecimenType(String name)
        throws Exception {
        return addSpecimenType(name, true);
    }

    public static int addSpecimenTypes(String name) throws Exception {
        int nber = r.nextInt(15) + 2;
        for (int i = 0; i < nber; i++) {
            addSpecimenType(name + i);
        }
        return nber;
    }

    public static void deleteCreatedSpecimenTypes() throws Exception {
        for (SpecimenTypeWrapper type : createdSpecimenTypes) {
            type.reload();
            type.delete();
        }
        createdSpecimenTypes.clear();
    }

    public static void removeFromCreated(SpecimenTypeWrapper type) {
        createdSpecimenTypes.remove(type);
    }

}
