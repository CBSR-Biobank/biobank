package edu.ualberta.med.biobank.action.util;

import edu.ualberta.med.biobank.model.IBiobankModel;

public class InfoUtil {

    public static boolean equals(IBiobankModel o1, IBiobankModel o2) {
        if (o1 == null) {
            return false;
        }
        if (o2 == null) {
            return false;
        }
        if (o1.getClass() != o2.getClass()) {
            return false;
        }
        Integer id = o1.getId();
        Integer id2 = o2.getId();
        if (id == null && id2 == null) {
            return o1 == o2;
        }
        return id != null && id2 != null && id.equals(id2);
    }
}
