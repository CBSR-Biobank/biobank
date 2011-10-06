package edu.ualberta.med.biobank.widgets.infotables.entry;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.widgets.infotables.BiobankTableSorter;
import edu.ualberta.med.biobank.widgets.infotables.NewSpecimenInfoTable;

public class NewSpecimenEntryInfoTable extends NewSpecimenInfoTable {

    protected List<Specimen> addedorModifiedSpecimens = new ArrayList<Specimen>();
    protected List<Specimen> removedSpecimens = new ArrayList<Specimen>();

    // list of what is displayed into the table
    protected List<SpecimenInfo> currentSpecimens;

    public NewSpecimenEntryInfoTable(Composite parent,
        List<SpecimenInfo> specInfos, ColumnsShown columnsShowns) {
        super(parent, specInfos, columnsShowns, 10);
        setCurrentSpecimens(specInfos);
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
                    SpecimenInfo i1 = (SpecimenInfo) getCollectionModelObject(e1);
                    SpecimenInfo i2 = (SpecimenInfo) getCollectionModelObject(e2);
                    return super.compare(i1.specimen.getInventoryId(),
                        i2.specimen.getInventoryId());
                } catch (Exception e) {
                    return 0;
                }
            }
        };
    }

    public void reload(List<SpecimenInfo> specimens) {
        setCurrentSpecimens(specimens);
        reloadCollection(currentSpecimens);
        addedorModifiedSpecimens = new ArrayList<Specimen>();
        removedSpecimens = new ArrayList<Specimen>();
    }

    public List<Specimen> getAddedOrModifiedSpecimens() {
        return addedorModifiedSpecimens;
    }

    public List<Specimen> getRemovedSpecimens() {
        return removedSpecimens;
    }

    private void setCurrentSpecimens(List<SpecimenInfo> specInfos) {
        currentSpecimens = new ArrayList<SpecimenInfo>();
        if (specInfos != null) {
            currentSpecimens.addAll(specInfos);
        }
    }
}
