package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class ShippingMethodInfoTable extends
    InfoTableWidget<ShippingMethodWrapper> {

    @SuppressWarnings("unused")
    private class TableSorter extends BiobankTableSorter {
        @Override
        public int compare(Viewer viewer, Object e1, Object e2) {
            ShippingMethodWrapper i1 = (ShippingMethodWrapper) ((BiobankCollectionModel) e1).o;
            ShippingMethodWrapper i2 = (ShippingMethodWrapper) ((BiobankCollectionModel) e2).o;
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

    private static final String[] HEADINGS = new String[] { "Shipping method" };

    private static final int[] BOUNDS = new int[] { 300, -1, -1, -1, -1, -1, -1 };

    public ShippingMethodInfoTable(Composite parent,
        List<ShippingMethodWrapper> sampleStorageCollection) {
        super(parent, sampleStorageCollection, HEADINGS, BOUNDS, 10);
    }

    @Override
    protected BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                ShippingMethodWrapper item = (ShippingMethodWrapper) ((BiobankCollectionModel) element).o;
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
        // return new TableSorter();
        return null;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((ShippingMethodWrapper) o).getName();
    }

    @Override
    public List<ShippingMethodWrapper> getCollection() {
        List<ShippingMethodWrapper> result = new ArrayList<ShippingMethodWrapper>();
        for (BiobankCollectionModel item : model) {
            result.add((ShippingMethodWrapper) item.o);
        }
        return result;
    }

    @Override
    public ShippingMethodWrapper getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        ShippingMethodWrapper shipping = (ShippingMethodWrapper) item.o;
        Assert.isNotNull(shipping);
        return shipping;
    }
}