package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.DispatchSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;

public abstract class DispatchSpecimenListInfoTable extends
    InfoTableWidget<DispatchSpecimenWrapper> {

    protected static class TableRowData {
        DispatchSpecimenWrapper dsa;
        String inventoryId;
        String type;
        String pnumber;
        String status;
        String comment;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { inventoryId, type, pnumber,
                status, comment }, "\t"); //$NON-NLS-1$
        }
    }

    private static final String[] HEADINGS = new String[] {
        Messages.DispatchSpecimenListInfoTable_inventoryid_label,
        Messages.DispatchSpecimenListInfoTable_type_label,
        Messages.DispatchSpecimenListInfoTable_pnumber_label,
        Messages.DispatchSpecimenListInfoTable_status_label,
        Messages.DispatchSpecimenListInfoTable_comment_label };

    private boolean editMode = false;

    public DispatchSpecimenListInfoTable(Composite parent,
        final DispatchWrapper shipment, boolean editMode) {
        super(parent, null, HEADINGS, 15, DispatchSpecimenWrapper.class);
        setList(getInternalDispatchSpecimens());
        this.editMode = editMode;
        if (editMode) {
            if (shipment.isInCreationState()) {
                addDeleteItemListener(new IInfoTableDeleteItemListener<DispatchSpecimenWrapper>() {
                    @Override
                    public void deleteItem(
                        InfoTableEvent<DispatchSpecimenWrapper> event) {
                        List<DispatchSpecimenWrapper> dsaList = getSelectedItems();
                        if (dsaList.size() > 0) {
                            if (dsaList.size() == 1
                                && !BgcPlugin
                                    .openConfirm(
                                        Messages.DispatchSpecimenListInfoTable_remove_confirm_title,
                                        NLS.bind(
                                            Messages.DispatchSpecimenListInfoTable_remove_one_confirm_msg,
                                            dsaList.get(0).getSpecimen()
                                                .getInventoryId())))
                                return;
                            if (dsaList.size() > 1
                                && !BgcPlugin
                                    .openConfirm(
                                        Messages.DispatchSpecimenListInfoTable_remove_confirm_title,
                                        NLS.bind(
                                            Messages.DispatchSpecimenListInfoTable_remove_multiple_confirm_msg,
                                            dsaList.size())))
                                return;
                            try {
                                shipment.removeDispatchSpecimens(dsaList);
                                reloadCollection();
                                notifyListeners();
                            } catch (Exception e) {
                                BgcPlugin
                                    .openAsyncError(
                                        Messages.DispatchSpecimenListInfoTable_delete_error_title,
                                        e);
                            }
                        }
                    }
                });
            }
        }
    }

    public abstract List<DispatchSpecimenWrapper> getInternalDispatchSpecimens();

    @Override
    protected boolean isEditMode() {
        return editMode;
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData info = (TableRowData) ((BiobankCollectionModel) element).o;
                if (info == null) {
                    if (columnIndex == 0) {
                        return Messages.infotable_loading_msg;
                    }
                    return Messages.DispatchSpecimenListInfoTable_14;
                }
                switch (columnIndex) {
                case 0:
                    return info.inventoryId;
                case 1:
                    return info.type;
                case 2:
                    return info.pnumber;
                case 3:
                    return info.status;
                case 4:
                    return info.comment;
                default:
                    return Messages.DispatchSpecimenListInfoTable_15;
                }
            }
        };
    }

    @Override
    public TableRowData getCollectionModelObject(Object obj) throws Exception {
        TableRowData info = new TableRowData();
        info.dsa = (DispatchSpecimenWrapper) obj;
        info.inventoryId = info.dsa.getSpecimen().getInventoryId();
        info.pnumber = info.dsa.getSpecimen().getCollectionEvent().getPatient()
            .getPnumber();
        SpecimenTypeWrapper type = info.dsa.getSpecimen().getSpecimenType();
        Assert.isNotNull(type, Messages.DispatchSpecimenListInfoTable_16);
        info.type = type.getName();
        info.status = info.dsa.getSpecimen().getActivityStatus().toString();
        info.comment = info.dsa.getCommentCollection(false).toString();
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        TableRowData r = (TableRowData) o;
        return r.toString();
    }

    public void setSelection(DispatchSpecimenWrapper selectedSample) {
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
    public DispatchSpecimenWrapper getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        TableRowData row = (TableRowData) item.o;
        Assert.isNotNull(row);
        return row.dsa;
    }

    public List<DispatchSpecimenWrapper> getSelectedItems() {
        Assert.isTrue(!tableViewer.getTable().isDisposed(),
            Messages.DispatchSpecimenListInfoTable_17);
        IStructuredSelection stSelection = (IStructuredSelection) tableViewer
            .getSelection();
        List<DispatchSpecimenWrapper> dsaList = new ArrayList<DispatchSpecimenWrapper>();

        for (Iterator<?> iter = stSelection.iterator(); iter.hasNext();) {
            BiobankCollectionModel bcm = (BiobankCollectionModel) iter.next();
            if (bcm != null) {
                TableRowData row = (TableRowData) bcm.o;
                Assert.isNotNull(row);
                dsaList.add(row.dsa);
            }
        }
        return dsaList;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }

    public void reloadCollection() {
        List<DispatchSpecimenWrapper> dsaList = getInternalDispatchSpecimens();
        if (dsaList == null) {
            dsaList = new ArrayList<DispatchSpecimenWrapper>();
        }
        reloadCollection(dsaList);
    }

}
