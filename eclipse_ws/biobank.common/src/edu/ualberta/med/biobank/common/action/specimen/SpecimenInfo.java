package edu.ualberta.med.biobank.common.action.specimen;

import java.io.Serializable;

import edu.ualberta.med.biobank.common.action.util.InfoUtil;
import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.model.Specimen;

public class SpecimenInfo implements Serializable, NotAProxy,
    Comparable<SpecimenInfo> {

    private static final long serialVersionUID = 1L;

    public Specimen specimen;
    public String parentLabel;
    public String positionString;
    public String topContainerTypeNameShort;

    public String getPositionString(boolean fullString,
        boolean addTopParentShortName) {
        if (positionString == null) {
            return null;
        }

        String position = positionString;
        if (fullString) {
            position = parentLabel + position;
        }
        if (addTopParentShortName)
            position += " (" + topContainerTypeNameShort + ")"; //$NON-NLS-1$ //$NON-NLS-2$
        return position;
    }

    @Override
    public int compareTo(SpecimenInfo info) {
        String s1 = getPositionString(true, true);
        String s2 = info.getPositionString(true, true);
        if (s1 == null || s2 == null)
            return specimen.getInventoryId().compareTo(
                info.specimen.getInventoryId());
        else
            return s1.compareTo(s2);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SpecimenInfo) {
            SpecimenInfo sInfo = (SpecimenInfo) o;
            if (this == sInfo)
                return true;
            return InfoUtil.equals(specimen, sInfo.specimen);
        }
        return false;
    }
}
