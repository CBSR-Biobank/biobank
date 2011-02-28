package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.SourceVesselWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class SourceVesselInfoTable extends InfoTableWidget<SourceVesselWrapper> {

    private static final String[] HEADINGS = new String[] { "Source vessel" };

    public SourceVesselInfoTable(Composite parent,
        List<SourceVesselWrapper> sampleStorageCollection) {
        super(parent, sampleStorageCollection, HEADINGS, 10);
    }

    @Override
    protected BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                SourceVesselWrapper item = (SourceVesselWrapper) ((BiobankCollectionModel) element).o;
                if (item == null) {
                    if (columnIndex == 0) {
                        return "loading...";
                    }
                    return "";
                }
                switch (columnIndex) {
                case 0:
                    return item.getSourceVesselType().getName();
                default:
                    return "";
                }
            }
        };
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((SourceVesselWrapper) o).getSourceVesselType().getName();
    }

    @Override
    public SourceVesselWrapper getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        SourceVesselWrapper source = (SourceVesselWrapper) item.o;
        Assert.isNotNull(source);
        return source;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }
}