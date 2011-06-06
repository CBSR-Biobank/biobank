package edu.ualberta.med.biobank.widgets.infotables.entry;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.widgets.infotables.BiobankTableSorter;
import edu.ualberta.med.biobank.widgets.infotables.SpecimenInfoTable;

public class SpecimenEntryInfoTable extends SpecimenInfoTable {

    protected List<SpecimenWrapper> addedorModifiedSpecimens = new ArrayList<SpecimenWrapper>();
    protected List<SpecimenWrapper> removedSpecimens = new ArrayList<SpecimenWrapper>();

    protected List<SpecimenWrapper> currentSpecimens;

    public SpecimenEntryInfoTable(Composite parent,
        List<SpecimenWrapper> specs, ColumnsShown columnsShowns) {
        super(parent, specs, columnsShowns, 10);
        currentSpecimens = new ArrayList<SpecimenWrapper>();

        if (specs != null) {
            currentSpecimens.addAll(specs);
        }
    }

    @Override
    protected boolean isEditMode() {
        return true;
    }

    @SuppressWarnings("serial")
    @Override
    protected BiobankTableSorter getComparator() {
        return new BiobankTableSorter() {
            @Override
            public int compare(Object e1, Object e2) {
                try {
                    TableRowData i1 = getCollectionModelObject((SpecimenWrapper) e1);
                    TableRowData i2 = getCollectionModelObject((SpecimenWrapper) e2);
                    return super.compare(i1.inventoryId, i2.inventoryId);
                } catch (Exception e) {
                    return 0;
                }
            }
        };
    }

    public void reload(List<SpecimenWrapper> specimens) {
        currentSpecimens = specimens;
        if (currentSpecimens == null) {
            currentSpecimens = new ArrayList<SpecimenWrapper>();
        }
        reloadCollection(currentSpecimens);
        addedorModifiedSpecimens = new ArrayList<SpecimenWrapper>();
        removedSpecimens = new ArrayList<SpecimenWrapper>();
    }

    public List<SpecimenWrapper> getAddedOrModifiedSpecimens() {
        return addedorModifiedSpecimens;
    }

    public List<SpecimenWrapper> getRemovedSpecimens() {
        return removedSpecimens;
    }

}
