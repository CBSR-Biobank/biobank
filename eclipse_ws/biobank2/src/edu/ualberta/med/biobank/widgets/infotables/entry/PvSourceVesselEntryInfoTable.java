package edu.ualberta.med.biobank.widgets.infotables.entry;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.PvSourceVesselWrapper;
import edu.ualberta.med.biobank.widgets.infotables.PvSourceVesselInfoTable;

public class PvSourceVesselEntryInfoTable extends PvSourceVesselInfoTable {

    public PvSourceVesselEntryInfoTable(Composite parent,
        List<PvSourceVesselWrapper> collection) {
        super(parent, collection);
    }

    @Override
    protected boolean isEditMode() {
        return true;
    }

}
