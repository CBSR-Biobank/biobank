package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class AliquotListInfoTable extends InfoTableWidget<AliquotWrapper> {

    public enum ColumnsShown {
        DEFAULT, PNUMBER
    }

    protected class TableRowData {
        AliquotWrapper aliquot;
        String inventoryId;
        String type;
        String position;
        String linkDate;
        Double quantity;
        String activityStatus;
        String comment;
        String pnumber;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { inventoryId, type, position,
                linkDate, (quantity != null) ? quantity.toString() : "",
                activityStatus, comment }, "\t");
        }
    }

    private static final String[] HEADINGS_DFLT = new String[] {
        "Inventory ID", "Type", "Position", "Link Date", "Quantity (ml)",
        "Activity Status", "Comment" };

    private static final String[] HEADINGS_PNUMBER = new String[] {
        "Inventory ID", "Type", "Position", "Link Date", "Patient Number",
        "Activity Status", "Comment" };

    private boolean showPatientNumber;

    public AliquotListInfoTable(Composite parent,
        List<AliquotWrapper> aliquotCollection) {
        super(parent, aliquotCollection, HEADINGS_DFLT, 20);
    }

    public AliquotListInfoTable(Composite parent,
        List<AliquotWrapper> aliquotCollection, ColumnsShown columnsShown) {
        super(parent, aliquotCollection,
            (columnsShown == ColumnsShown.PNUMBER) ? HEADINGS_PNUMBER
                : HEADINGS_DFLT, 20);
        this.showPatientNumber = (columnsShown == ColumnsShown.PNUMBER);
    }

    @Override
    protected BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData info = (TableRowData) ((BiobankCollectionModel) element).o;
                if (info == null) {
                    if (columnIndex == 0) {
                        return "loading...";
                    }
                    return "";
                }
                switch (columnIndex) {
                case 0:
                    return info.inventoryId;
                case 1:
                    return info.type;
                case 2:
                    return info.position;
                case 3:
                    return info.linkDate;
                case 4:
                    if (showPatientNumber) {
                        return info.pnumber;
                    }
                    return (info.quantity != null) ? info.quantity.toString()
                        : "";
                case 5:
                    return info.activityStatus;
                case 6:
                    return info.comment;
                default:
                    return "";
                }
            }
        };
    }

    @Override
    public TableRowData getCollectionModelObject(AliquotWrapper aliquot)
        throws Exception {
        TableRowData info = new TableRowData();
        info.aliquot = aliquot;
        info.inventoryId = aliquot.getInventoryId();
        SampleTypeWrapper type = aliquot.getSampleType();
        Assert.isNotNull(type, "aliquot with null for sample type");
        info.type = type.getName();
        info.position = aliquot.getPositionString();
        info.linkDate = DateFormatter.formatAsDateTime(aliquot.getLinkDate());
        info.quantity = aliquot.getQuantity();
        info.activityStatus = aliquot.getActivityStatus().getName();
        info.comment = aliquot.getComment();

        if (showPatientNumber) {
            info.pnumber = aliquot.getPatientVisit().getPatient().getPnumber();
        }
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        TableRowData r = (TableRowData) o;
        if (showPatientNumber) {
            return StringUtils.join(
                new String[] { r.inventoryId, r.type, r.position, r.linkDate,
                    r.pnumber, r.activityStatus, r.comment }, "\t");
        }
        return r.toString();
    }

    public void setSelection(AliquotWrapper selectedSample) {
        if (selectedSample == null)
            return;
        for (BiobankCollectionModel item : model) {
            TableRowData info = (TableRowData) item.o;
            if (info.aliquot == selectedSample) {
                getTableViewer().setSelection(new StructuredSelection(item),
                    true);
            }
        }
    }

    @Override
    public AliquotWrapper getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        TableRowData row = (TableRowData) item.o;
        Assert.isNotNull(row);
        return row.aliquot;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }
}
