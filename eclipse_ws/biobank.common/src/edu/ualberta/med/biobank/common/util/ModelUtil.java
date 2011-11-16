package edu.ualberta.med.biobank.common.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.model.IBiobankModel;

public class ModelUtil {

    public static Set<Integer> getCollectionIds(Collection<? extends IBiobankModel> collection) {
        Set<Integer> ids = new HashSet<Integer>();

        for (IBiobankModel model : collection) {
            ids.add(model.getId());
        }
        return ids;
    }
    
}
