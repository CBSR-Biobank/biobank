package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class SpecimenTypeInfoTable extends InfoTableWidget<SpecimenTypeWrapper> {

    private static final String[] HEADINGS = new String[] { "Name",
        "Short Name" };

    public SpecimenTypeInfoTable(Composite parent,
        List<SpecimenTypeWrapper> specimenCollection) {
        super(parent, specimenCollection, HEADINGS, 20);
    }

    @Override
    protected BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                SpecimenTypeWrapper item = (SpecimenTypeWrapper) ((BiobankCollectionModel) element).o;
                if (item == null) {
                    if (columnIndex == 0) {
                        return "loading...";
                    }
                    return "";
                }
                switch (columnIndex) {
                case 0:
                    return item.getName();
                case 1:
                    return item.getNameShort();
                default:
                    return null;
                }
            }
        };
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        // if (o == null)
        // return null;
        // return ((SourceVesselWrapper) o).getSourceVesselType().getName();
        return null;
    }

    @Override
    public SpecimenTypeWrapper getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        SpecimenTypeWrapper source = (SpecimenTypeWrapper) item.o;
        Assert.isNotNull(source);
        return source;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }
}