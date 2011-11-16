package edu.ualberta.med.biobank.common.action.specimen;

import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.util.InfoUtil;
import edu.ualberta.med.biobank.model.Specimen;

public class SpecimenInfo implements ActionResult {

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
