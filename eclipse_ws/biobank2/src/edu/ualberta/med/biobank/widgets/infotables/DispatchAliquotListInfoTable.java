package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MenuItem;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.util.DispatchAliquotState;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentAliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public abstract class DispatchAliquotListInfoTable extends
    InfoTableWidget<DispatchShipmentAliquotWrapper> {

    protected class TableRowData {
        DispatchShipmentAliquotWrapper dsa;
        String inventoryId;
        String type;
        String pnumber;
        DispatchAliquotState state;
        String comment;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { inventoryId, type, pnumber,
                state.toString(), comment }, "\t");
        }
    }

    private static final String[] HEADINGS = new String[] { "Inventory ID",
        "Type", "Patient Number", "State", "Comment" };

    private static final int[] BOUNDS = new int[] { 100, 100, 120, 120, -1 };

    private boolean editMode = false;

    private DispatchShipmentWrapper currentShipment;

    public DispatchAliquotListInfoTable(Composite parent,
        final DispatchShipmentWrapper shipment, boolean editMode) {
        super(parent, null, HEADINGS, BOUNDS, 15);
        this.currentShipment = shipment;
        setCollection(getInternalDispatchShipmentAliquots());
        this.editMode = editMode;
        if (editMode) {
            if (shipment.isInCreationState()) {
                addDeleteItemListener(new IInfoTableDeleteItemListener() {
                    @Override
                    public void deleteItem(InfoTableEvent event) {
                        DispatchShipmentAliquotWrapper dsa = getSelection();
                        if (dsa != null) {
                            if (!BioBankPlugin.openConfirm("Remove Aliquot",
                                "Are you sure you want to remove aliquot \""
                                    + dsa.getAliquot().getInventoryId()
                                    + "\" from this shipment ?"))
                                return;
                            try {
                                shipment.removeDispatchShipmentAliquots(Arrays
                                    .asList(getSelection()));
                                reloadCollection();
                                notifyListeners();
                            } catch (Exception e) {
                                BioBankPlugin
                                    .openAsyncError("Delete failed", e);
                            }
                        }
                    }
                });
            } else {
                MenuItem item = new MenuItem(menu, SWT.PUSH);
                item.setText("Set as missing-pending");
                item.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        setItemMissingPending();
                    }
                });
                item = new MenuItem(menu, SWT.PUSH);
                item.setText("Set as missing");
                item.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        setItemMissing();
                    }
                });
            }
        }
    }

    protected void setItemMissing() {
        modifyState(DispatchAliquotState.MISSING);
    }

    protected void setItemMissingPending() {
        modifyState(DispatchAliquotState.MISSING_PENDING_STATE);
    }

    private void modifyState(DispatchAliquotState newState) {
        DispatchShipmentAliquotWrapper dsa = getSelection();
        dsa.setState(newState.ordinal());
        currentShipment.resetStateLists();
        notifyListeners();
    }

    public abstract List<DispatchShipmentAliquotWrapper> getInternalDispatchShipmentAliquots();

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
                    return info.state.toString();
                case 4:
                    return info.comment;
                default:
                    return "";
                }
            }
        };
    }

    @Override
    public TableRowData getCollectionModelObject(
        DispatchShipmentAliquotWrapper dsa) throws Exception {
        TableRowData info = new TableRowData();
        info.dsa = dsa;
        info.inventoryId = dsa.getAliquot().getInventoryId();
        info.pnumber = dsa.getAliquot().getPatientVisit().getPatient()
            .getPnumber();
        SampleTypeWrapper type = dsa.getAliquot().getSampleType();
        Assert.isNotNull(type, "aliquot with null for sample type");
        info.type = type.getName();
        info.state = DispatchAliquotState.getState(dsa.getState());
        info.comment = dsa.getComment();
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        TableRowData r = (TableRowData) o;
        return r.toString();
    }

    public void setSelection(DispatchShipmentAliquotWrapper selectedSample) {
        if (selectedSample == null)
            return;
        for (BiobankCollectionModel item : model) {
            TableRowData info = (TableRowData) item.o;
            if (info.dsa == selectedSample) {
                getTableViewer().setSelection(new StructuredSelection(item),
                    true);
            }
        }
    }

    @Override
    public DispatchShipmentAliquotWrapper getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        TableRowData row = (TableRowData) item.o;
        Assert.isNotNull(row);
        return row.dsa;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }

    public void reloadCollection() {
        reloadCollection(getInternalDispatchShipmentAliquots());
    }

}
