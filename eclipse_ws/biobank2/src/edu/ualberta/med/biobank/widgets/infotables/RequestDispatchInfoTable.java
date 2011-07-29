package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class RequestDispatchInfoTable extends InfoTableWidget<DispatchWrapper> {

    private static final String[] HEADINGS = new String[] { "Sender",
        "Receiver", "Total Specimens", "Date Packed", "Date Received", "State" };

    public RequestDispatchInfoTable(Composite parent,
        List<DispatchWrapper> dispatchCollection) {
        super(parent, dispatchCollection, HEADINGS, 10, DispatchWrapper.class);
    }

    @Override
    protected BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                DispatchWrapper item = (DispatchWrapper) ((BiobankCollectionModel) element).o;
                if (item == null) {
                    if (columnIndex == 0) {
                        return "loading...";
                    }
                    return "";
                }
                switch (columnIndex) {
                case 0:
                    return item.getSenderCenter().getNameShort();
                case 1:
                    return item.getReceiverCenter().getNameShort();
                case 2:
                    return ((Integer) item.getDispatchSpecimenCollection(false)
                        .size()).toString();
                case 3:
                    return item.getFormattedPackedAt();
                case 4:
                    return item.getFormattedReceivedAt();
                case 5:
                    return item.getStateDescription();
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
        return ((DispatchWrapper) o).toString();
    }

    @Override
    public DispatchWrapper getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        DispatchWrapper d = (DispatchWrapper) item.o;
        Assert.isNotNull(d);
        return d;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }

    public void setSelection(Object object) {
        getTableViewer().setSelection((ISelection) object);
    }

    @Override
    public boolean isEditMode() {
        return true;
    }

}