package edu.ualberta.med.biobank.widgets.specimentypeselection;

import edu.ualberta.med.biobank.common.action.scanprocess.SpecimenHierarchyInfo;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Specimen;

public class SpecimenTypeSelectionEvent extends SpecimenHierarchyInfo {
    private final Integer rowNumber;

    public SpecimenTypeSelectionEvent(Integer rowNumber, Specimen parentSpecimen,
        AliquotedSpecimen aliquotedSpecimenType) {
        super(parentSpecimen, aliquotedSpecimenType);
        this.rowNumber = rowNumber;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

}
