package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class DispatchAliquotListInfoTable extends
    InfoTableWidget<AliquotWrapper> {

    protected class TableRowData {
        AliquotWrapper aliquot;
        String inventoryId;
        String type;
        String pnumber;
        String activityStatus;
        String comment;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { inventoryId, type, pnumber,
                activityStatus, comment }, "\t");
        }
    }

    private static final String[] HEADINGS = new String[] { "Inventory ID",
        "Type", "Patient Number", "Activity Status", "Comment" };

    private static final int[] BOUNDS = new int[] { 100, 100, 120, 120, -1 };

    private boolean editMode = false;

    public DispatchAliquotListInfoTable(Composite parent,
        List<AliquotWrapper> aliquotCollection, boolean editMode) {
        super(parent, aliquotCollection, HEADINGS, BOUNDS, 20);
        if (editMode) {
            addDeleteItemListener(new IInfoTableDeleteItemListener() {
                @Override
                public void deleteItem(InfoTableEvent event) {
                    AliquotWrapper aliquot = getSelection();
                    if (aliquot != null) {
                        if (!BioBankPlugin.openConfirm("Remove Aliquot",
                            "Are you sure you want to remove aliquot \""
                                + aliquot.getInventoryId()
                                + "\" from this shipment ?"))
                            return;
                        System.out.println("remove aliquot");
                        // try {
                        // site.removeStudies(Arrays.asList(study));
                        // setCollection(site.getStudyCollection(true));
                        // notifyListeners();
                        // } catch (BiobankCheckException e) {
                        // BioBankPlugin.openAsyncError("Delete failed", e);
                        // }
                    }
                }
            });
        }
    }

    @Override
    protected boolean isEditMode() {
        return editMode;
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
                    return info.pnumber;
                case 3:
                    return info.activityStatus;
                case 4:
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
        info.pnumber = aliquot.getPatientVisit().getPatient().getPnumber();
        SampleTypeWrapper type = aliquot.getSampleType();
        Assert.isNotNull(type, "aliquot with null for sample type");
        info.type = type.getName();
        info.activityStatus = aliquot.getActivityStatus().getName();
        info.comment = aliquot.getComment();
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        TableRowData r = (TableRowData) o;
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
