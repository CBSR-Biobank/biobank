package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class SampleTypeInfoTable extends InfoTableWidget<SampleTypeWrapper> {

    private class TableSorter extends BiobankTableSorter {
        @Override
        public int compare(Viewer viewer, Object e1, Object e2) {
            SampleTypeWrapper i1 = (SampleTypeWrapper) ((BiobankCollectionModel) e1).o;
            SampleTypeWrapper i2 = (SampleTypeWrapper) ((BiobankCollectionModel) e2).o;
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

    private static final String[] HEADINGS = new String[] { "Sample Type",
        "Short Name" };

    private static final int[] BOUNDS = new int[] { 300, 130, -1, -1, -1, -1,
        -1 };

    public SampleTypeInfoTable(Composite parent,
        List<SampleTypeWrapper> sampleTypeCollection) {
        super(parent, sampleTypeCollection, HEADINGS, BOUNDS);
        setSorter(new TableSorter());
    }

    @Override
    public BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                SampleTypeWrapper item = (SampleTypeWrapper) ((BiobankCollectionModel) element).o;
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
                    return "";
                }
            }
        };
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        SampleTypeWrapper type = (SampleTypeWrapper) o;
        return type.getName() + "\t" + type.getNameShort();
    }

    @Override
    public List<SampleTypeWrapper> getCollection() {
        List<SampleTypeWrapper> result = new ArrayList<SampleTypeWrapper>();
        for (BiobankCollectionModel item : model) {
            result.add((SampleTypeWrapper) item.o);
        }
        return result;
    }

    @Override
    public SampleTypeWrapper getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        SampleTypeWrapper type = (SampleTypeWrapper) item.o;
        Assert.isNotNull(type);
        return type;
    }
}
