package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.SourceVesselWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class SourceVesselInfoTable extends InfoTableWidget<SourceVesselWrapper> {

    private class TableSorter extends BiobankTableSorter {
        @Override
        public int compare(Viewer viewer, Object e1, Object e2) {
            SourceVesselWrapper i1 = (SourceVesselWrapper) ((BiobankCollectionModel) e1).o;
            SourceVesselWrapper i2 = (SourceVesselWrapper) ((BiobankCollectionModel) e2).o;
            if (i1 == null) {
                return -1;
            } else if (i2 == null) {
                return 1;
            }
            int rc = 0;
            switch (propertyIndex) {
            case 0:
                rc = compare(i1.getName(), i2.getName());
                break;
            default:
                rc = 0;
            }
            // If descending order, flip the direction
            if (direction == 1) {
                rc = -rc;
            }
            return rc;
        }
    }

    private static final String[] HEADINGS = new String[] { "Source vessel" };

    private static final int[] BOUNDS = new int[] { 300, -1, -1, -1, -1, -1, -1 };

    public SourceVesselInfoTable(Composite parent,
        List<SourceVesselWrapper> sampleStorageCollection) {
        super(parent, sampleStorageCollection, HEADINGS, BOUNDS, 10);
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
                    return item.getName();
                default:
                    return "";
                }
            }
        };
    }

    @Override
    protected BiobankTableSorter getTableSorter() {
        return new TableSorter();
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((SourceVesselWrapper) o).getName();
    }

    @Override
    public List<SourceVesselWrapper> getCollection() {
        List<SourceVesselWrapper> result = new ArrayList<SourceVesselWrapper>();
        for (BiobankCollectionModel item : model) {
            result.add((SourceVesselWrapper) item.o);
        }
        return result;
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
}