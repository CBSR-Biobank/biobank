package edu.ualberta.med.biobank.model.util;

import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.model.IBiobankModel;

public class ModelUtil {
    public static Integer getId(IBiobankModel model) {
        return model != null ? model.getId() : null;
    }

    public static Set<Integer> getIds(Set<? extends IBiobankModel> models) {
        Set<Integer> ids = new HashSet<Integer>();
        for (IBiobankModel model : models) {
            ids.add(model.getId());
        }
        return ids;
    }
}
