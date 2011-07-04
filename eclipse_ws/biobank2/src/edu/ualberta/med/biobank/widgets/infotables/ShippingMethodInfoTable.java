package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class ShippingMethodInfoTable extends
    InfoTableWidget<ShippingMethodWrapper> {

    private static final String[] HEADINGS = new String[] { "Shipping method" };

    public ShippingMethodInfoTable(Composite parent,
        List<ShippingMethodWrapper> sampleStorageCollection) {
        super(parent, sampleStorageCollection, HEADINGS, 10,
            ShippingMethodWrapper.class);
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
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((ShippingMethodWrapper) o).getName();
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

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }
}