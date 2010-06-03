package edu.ualberta.med.biobank.widgets.infotables.entry;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.widgets.infotables.BiobankTableSorter;
import edu.ualberta.med.biobank.widgets.infotables.PatientInfoTable;

public class PatientEntryInfoTable extends PatientInfoTable {

    public PatientEntryInfoTable(Composite parent, List<PatientWrapper> patients) {
        super(parent, patients);
    }

    @Override
    protected boolean isEditMode() {
        return true;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return new BiobankTableSorter() {
            @Override
            public int compare(Object e1, Object e2) {
                try {
                    TableRowData i1 = getCollectionModelObject((PatientWrapper) e1);
                    TableRowData i2 = getCollectionModelObject((PatientWrapper) e2);
                    return super.compare(i1.pnumber, i2.pnumber);
                } catch (Exception e) {
                    return 0;
                }
            }
        };
    }

}
