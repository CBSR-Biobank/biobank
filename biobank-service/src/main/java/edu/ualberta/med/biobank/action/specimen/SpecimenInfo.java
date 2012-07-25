package edu.ualberta.med.biobank.action.specimen;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.action.ActionResult;
import edu.ualberta.med.biobank.action.util.InfoUtil;
import edu.ualberta.med.biobank.model.Specimen;

public class SpecimenInfo implements ActionResult {

    private static final long serialVersionUID = 1L;

    public Specimen specimen;
    public String parentLabel;
    public String positionString;
    public String topContainerTypeNameShort;
    public String comment;

    public SpecimenInfo(SpecimenInfo info) {
        this.specimen = info.specimen;
        this.parentLabel = info.parentLabel;
        this.positionString = info.positionString;
        this.comment = info.comment;
    }

    public SpecimenInfo() {
        this.specimen = new Specimen();
    }

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

    public static Set<Integer> getSpecimenIds(List<SpecimenInfo> specimenInfos) {
        HashSet<Integer> result = new HashSet<Integer>();
        for (SpecimenInfo specimenInfo : specimenInfos) {
            result.add(specimenInfo.specimen.getId());
        }
        return result;
    }

}
