package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.SampleSourceWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class SampleSourceInfoTable extends InfoTableWidget<SampleSourceWrapper> {

    private class TableSorter extends BiobankTableSorter {
        @Override
        public int compare(Viewer viewer, Object e1, Object e2) {
            SampleSourceWrapper i1 = (SampleSourceWrapper) ((BiobankCollectionModel) e1).o;
            SampleSourceWrapper i2 = (SampleSourceWrapper) ((BiobankCollectionModel) e2).o;
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

    public SampleSourceInfoTable(Composite parent,
        Collection<SampleSourceWrapper> sampleStorageCollection) {
        super(parent, sampleStorageCollection, HEADINGS, BOUNDS);
        setSorter(new TableSorter());
    }

    @Override
    public BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                SampleSourceWrapper item = (SampleSourceWrapper) ((BiobankCollectionModel) element).o;
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
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((SampleSourceWrapper) o).getName();
    }

    @Override
    public List<SampleSourceWrapper> getCollection() {
        List<SampleSourceWrapper> result = new ArrayList<SampleSourceWrapper>();
        for (BiobankCollectionModel item : model) {
            result.add((SampleSourceWrapper) item.o);
        }
        return result;
    }

    @Override
    public SampleSourceWrapper getSelection() {
        SampleSourceWrapper item = (SampleSourceWrapper) getSelectionInternal().o;
        Assert.isNotNull(item);
        return item;
    }
}