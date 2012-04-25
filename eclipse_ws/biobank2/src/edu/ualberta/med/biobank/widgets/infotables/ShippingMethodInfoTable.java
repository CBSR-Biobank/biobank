package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.AbstractInfoTableWidget;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.model.ShippingMethod;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ShippingMethodInfoTable extends
    InfoTableWidget<ShippingMethodWrapper> {

    private static final String[] HEADINGS =
        new String[] { ShippingMethod.NAME.singular().toString() };

    public ShippingMethodInfoTable(Composite parent,
        List<ShippingMethodWrapper> sampleStorageCollection) {
        super(parent, sampleStorageCollection, HEADINGS, 10,
            ShippingMethodWrapper.class);
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                ShippingMethodWrapper item =
                    (ShippingMethodWrapper) ((BiobankCollectionModel) element).o;
                if (item == null) {
                    if (columnIndex == 0) {
                        return AbstractInfoTableWidget.LOADING;
                    }
                    return StringUtil.EMPTY_STRING;
                }
                switch (columnIndex) {
                case 0:
                    return item.getName();
                default:
                    return StringUtil.EMPTY_STRING;
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

    @Override
    protected Boolean canEdit(ShippingMethodWrapper target)
        throws ApplicationException {
        return true;
    }

    @Override
    protected Boolean canDelete(ShippingMethodWrapper target)
        throws ApplicationException {
        return true;
    }

    @Override
    protected Boolean canView(ShippingMethodWrapper target)
        throws ApplicationException {
        return true;
    }
}