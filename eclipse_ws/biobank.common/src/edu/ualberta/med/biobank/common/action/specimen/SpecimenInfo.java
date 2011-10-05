package edu.ualberta.med.biobank.common.action.specimen;

import java.io.Serializable;

import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.model.Specimen;

public class SpecimenInfo implements Serializable, NotAProxy {

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
}
