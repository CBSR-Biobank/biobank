package edu.ualberta.med.biobank.common.action.specimen;

import java.io.Serializable;

import edu.ualberta.med.biobank.common.action.util.InfoUtil;
import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.model.SpecimenType;

public class SpecimenTypeInfo implements Serializable, NotAProxy,
    Comparable<SpecimenTypeInfo> {

    private static final long serialVersionUID = 1L;

    public SpecimenType type;

    @Override
    public int compareTo(SpecimenTypeInfo info) {
        String name1 = type.getName();
        String name2 = info.type.getName();
        if (name1 != null && name2 != null) {
            return name1.compareTo(name2);
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SpecimenTypeInfo) {
            SpecimenTypeInfo sInfo = (SpecimenTypeInfo) o;
            if (this == sInfo)
                return true;
            return InfoUtil.equals(type, sInfo.type);
        }
        return false;
    }
}
