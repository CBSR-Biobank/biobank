package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.AbstractInfoTableWidget;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Specimen;
import gov.nih.nci.system.applicationservice.ApplicationException;

public abstract class DispatchSpecimenListInfoTable extends
    InfoTableWidget<DispatchSpecimenWrapper> {
    public static final I18n i18n = I18nFactory
        .getI18n(DispatchSpecimenListInfoTable.class);

    protected static class TableRowData {
        DispatchSpecimenWrapper dsa;
        String inventoryId;
        String type;
        String pnumber;
        String status;
        String comment;

        @SuppressWarnings("nls")
        @Override
        public String toString() {
            return StringUtils.join(new String[] { inventoryId, type, pnumber,
                status, comment }, "\t");
        }
    }

    @SuppressWarnings("nls")
    private static final String[] HEADINGS = new String[] {
        Specimen.PropertyName.INVENTORY_ID.toString(),
        i18n.tr("Type"),
        Patient.PropertyName.PNUMBER.toString(),
        ActivityStatus.NAME.singular().toString(),
        Comment.NAME.singular().toString() };

    private boolean editMode = false;

    public DispatchSpecimenListInfoTable(Composite parent,
        final DispatchWrapper shipment, boolean editMode) {
        super(parent, null, HEADINGS, 15, DispatchSpecimenWrapper.class);
        setList(getInternalDispatchSpecimens());
        this.editMode = editMode;
        if (editMode) {
            if (shipment.isInCreationState()) {
                addDeleteItemListener(new IInfoTableDeleteItemListener<DispatchSpecimenWrapper>() {
                    @SuppressWarnings("nls")
                    @Override
                    public void deleteItem(
                        InfoTableEvent<DispatchSpecimenWrapper> event) {
                        List<DispatchSpecimenWrapper> dsaList =
                            getSelectedItems();
                        if (dsaList.size() > 0) {
                            if (dsaList.size() == 1
                                && !BgcPlugin
                                    .openConfirm(
                                        // dialog title.
                                        i18n.tr("Remove Specimen"),
                                        // dialog message.
                                        i18n.tr(
                                            "Are you sure you want to remove specimen \"{0}\" from this shipment ?",
                                            dsaList.get(0).getSpecimen()
                                                .getInventoryId())))
                                return;
                            if (dsaList.size() > 1
                                && !BgcPlugin
                                    .openConfirm(
                                        // dialog title.
                                        i18n.tr("Remove Specimen"),
                                        // dialog message.
                                        i18n.tr(
                                            "Are you sure you want to remove these {0} specimens from this shipment ?",
                                            dsaList.size())))
                                return;
                            try {
                                shipment.removeDispatchSpecimens(dsaList);
                                reloadCollection();
                                notifyListeners();
                            } catch (Exception e) {
                                BgcPlugin
                                    .openAsyncError(
                                        // dialog title.
                                        i18n.tr("Delete failed"),
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
                TableRowData info =
                    (TableRowData) ((BiobankCollectionModel) element).o;
                if (info == null) {
                    if (columnIndex == 0) {
                        return AbstractInfoTableWidget.LOADING;
                    }
                    return StringUtil.EMPTY_STRING;
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
                    return StringUtil.EMPTY_STRING;
                }
            }
        };
    }

    @SuppressWarnings("nls")
    @Override
    public TableRowData getCollectionModelObject(Object obj) throws Exception {
        TableRowData info = new TableRowData();
        info.dsa = (DispatchSpecimenWrapper) obj;
        info.inventoryId = info.dsa.getSpecimen().getInventoryId();
        info.pnumber = info.dsa.getSpecimen().getCollectionEvent().getPatient()
            .getPnumber();
        SpecimenTypeWrapper type = info.dsa.getSpecimen().getSpecimenType();
        Assert.isNotNull(type, "specimen with null type");
        info.type = type.getName();
        info.status = info.dsa.getSpecimen().getActivityStatus().toString();
        info.comment =
            CommentWrapper.commentListToString(info.dsa
                .getCommentCollection(false));
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

    @SuppressWarnings("nls")
    public List<DispatchSpecimenWrapper> getSelectedItems() {
        Assert.isTrue(!tableViewer.getTable().isDisposed(),
            "widget is disposed");
        IStructuredSelection stSelection = (IStructuredSelection) tableViewer
            .getSelection();
        List<DispatchSpecimenWrapper> dsaList =
            new ArrayList<DispatchSpecimenWrapper>();

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
        setList(dsaList);
    }

    @Override
    protected Boolean canEdit(DispatchSpecimenWrapper target)
        throws ApplicationException {
        return false;
    }

    @Override
    protected Boolean canDelete(DispatchSpecimenWrapper target)
        throws ApplicationException {
        return true;
    }

    @Override
    protected Boolean canView(DispatchSpecimenWrapper target)
        throws ApplicationException {
        return true;
    }

}
