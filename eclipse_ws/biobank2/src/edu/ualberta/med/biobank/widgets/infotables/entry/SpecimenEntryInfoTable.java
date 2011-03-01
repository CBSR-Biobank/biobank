package edu.ualberta.med.biobank.widgets.infotables.entry;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.widgets.infotables.BiobankTableSorter;
import edu.ualberta.med.biobank.widgets.infotables.SpecimenInfoTable;

public class SpecimenEntryInfoTable extends SpecimenInfoTable {

    public SpecimenEntryInfoTable(Composite parent, List<SpecimenWrapper> specs) {
        super(parent, specs);
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
                    TableRowData i1 = getCollectionModelObject((SpecimenWrapper) e1);
                    TableRowData i2 = getCollectionModelObject((SpecimenWrapper) e2);
                    return super.compare(i1.pnumber, i2.pnumber);
                } catch (Exception e) {
                    return 0;
                }
            }
        };
    }

}
