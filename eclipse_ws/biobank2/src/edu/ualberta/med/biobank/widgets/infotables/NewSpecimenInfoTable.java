package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.formatters.NumberFormatter;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.util.AdapterFactory;

public class NewSpecimenInfoTable extends InfoTableWidget<SpecimenInfo> {

    public static enum ColumnsShown {
        // ALL(new String[] { Messages.SpecimenInfoTable_inventoryid_label,
        // Messages.SpecimenInfoTable_type_label,
        // Messages.SpecimenInfoTable_patient_label,
        // Messages.SpecimenInfoTable_visitNber_label,
        // Messages.SpecimenInfoTable_origin_center_label,
        // Messages.SpecimenInfoTable_current_center_label,
        // Messages.SpecimenInfoTable_position_label,
        // Messages.SpecimenInfoTable_created_label,
        // Messages.SpecimenInfoTable_quantity_label,
        // Messages.SpecimenInfoTable_status_label,
        // Messages.SpecimenInfoTable_comments_label }) {
        // @Override
        // public String getColumnValue(SpecimenInfo row, int columnIndex) {
        // switch (columnIndex) {
        // case 0:
        // return row.specimen.inventoryId;
        // case 1:
        // return row.specimen.type;
        // case 2:
        // return row.specimen.getCollectionEvent().getPatient();
        // case 3:
        // return row.pvNumber;
        // case 4:
        // return row.originCenter;
        // case 5:
        // return row.center;
        // case 6:
        // return row.position;
        // case 7:
        // return row.createdAt;
        // case 8:
        // return NumberFormatter.format(row.quantity);
        // case 9:
        // return row.activityStatus;
        // case 10:
        // return row.comment;
        // default:
        //                    return ""; //$NON-NLS-1$
        // }
        // }
        //
        // @Override
        // public Image getColumnImage(TableRowData row, int columnIndex) {
        // if (columnIndex == 9
        // && ActivityStatusWrapper.FLAGGED_STATUS_STRING
        // .equals(row.activityStatus))
        // return BgcPlugin.getDefault().getImageRegistry()
        // .get(BgcPlugin.IMG_ERROR);
        // return null;
        // }
        // },
        PEVENT_SOURCE_SPECIMENS(new String[] {
            Messages.SpecimenInfoTable_inventoryid_label,
            Messages.SpecimenInfoTable_type_label,
            Messages.SpecimenInfoTable_position_label,
            Messages.SpecimenInfoTable_time_drawn_label,
            Messages.SpecimenInfoTable_quantity_ml_label,
            Messages.SpecimenInfoTable_status_label,
            Messages.SpecimenInfoTable_study_label,
            Messages.SpecimenInfoTable_pnumber_label,
            Messages.SpecimenInfoTable_origin_center_label,
            Messages.SpecimenInfoTable_current_center_label,
            Messages.SpecimenInfoTable_comments_label }) {
            @Override
            public String getColumnValue(SpecimenInfo row, int columnIndex) {
                switch (columnIndex) {
                case 0:
                    return row.specimen.getInventoryId();
                case 1:
                    return row.specimen.getSpecimenType().getNameShort();
                case 2:
                    return row.getPositionString(true, true);
                case 3:
                    return DateFormatter.formatAsDateTime(row.specimen
                        .getCreatedAt());
                case 4:
                    return NumberFormatter.format(row.specimen.getQuantity());
                case 5:
                    return row.specimen.getActivityStatus().getName();
                case 6:
                    return row.specimen.getCollectionEvent().getPatient()
                        .getStudy().getNameShort();
                case 7:
                    return row.specimen.getCollectionEvent().getPatient()
                        .getPnumber();
                case 8:
                    OriginInfo oi = row.specimen.getOriginInfo();
                    return oi == null ? "" : oi.getCenter().getNameShort(); //$NON-NLS-1$
                case 9:
                    return row.specimen.getCurrentCenter().getNameShort();
                case 10:
                    return getCommentLetter(row);
                default:
                    return ""; //$NON-NLS-1$
                }
            }

            @Override
            public Image getColumnImage(SpecimenInfo row, int columnIndex) {
                if (columnIndex == 5
                    && ActivityStatus.FLAGGED == row.specimen
                        .getActivityStatus())
                    return BgcPlugin.getDefault().getImageRegistry()
                        .get(BgcPlugin.IMG_ERROR);
                return null;
            }
        },
        CEVENT_SOURCE_SPECIMENS(new String[] {
            Messages.SpecimenInfoTable_inventoryid_label,
            Messages.SpecimenInfoTable_type_label,
            Messages.SpecimenInfoTable_position_label,
            Messages.SpecimenInfoTable_time_drawn_label,
            Messages.NewSpecimenInfoTable_quantity_label,
            Messages.SpecimenInfoTable_status_label,
            Messages.SpecimenInfoTable_worksheet_label,
            Messages.SpecimenInfoTable_origin_center_label,
            Messages.SpecimenInfoTable_current_center_label,
            Messages.SpecimenInfoTable_comments_label }) {
            @Override
            public String getColumnValue(SpecimenInfo row, int columnIndex) {
                switch (columnIndex) {
                case 0:
                    return row.specimen.getInventoryId();
                case 1:
                    return row.specimen.getSpecimenType().getNameShort();
                case 2:
                    return row.getPositionString(true, true);
                case 3:
                    return DateFormatter.formatAsDateTime(row.specimen
                        .getCreatedAt());
                case 4:
                    return NumberFormatter.format(row.specimen.getQuantity());
                case 5:
                    return row.specimen.getActivityStatus().getName();
                case 6:
                    ProcessingEvent pe = row.specimen.getProcessingEvent();
                    return pe == null ? "" : pe.getWorksheet(); //$NON-NLS-1$
                case 7:
                    OriginInfo oi = row.specimen.getOriginInfo();
                    return oi == null ? "" : oi.getCenter().getNameShort(); //$NON-NLS-1$
                case 8:
                    return row.specimen.getCurrentCenter().getNameShort();
                case 9:
                    return getCommentLetter(row);
                default:
                    return ""; //$NON-NLS-1$
                }
            }

            @Override
            public Image getColumnImage(SpecimenInfo row, int columnIndex) {
                // FIXME retrieve this info as an enum in SpecimenInfo ?
                if (columnIndex == 5
                    && ActivityStatus.FLAGGED == row.specimen
                        .getActivityStatus())
                    return BgcPlugin.getDefault().getImageRegistry()
                        .get(BgcPlugin.IMG_ERROR);
                return null;
            }
        },

        CEVENT_ALIQUOTED_SPECIMENS(new String[] {
            Messages.SpecimenInfoTable_inventoryid_label,
            Messages.SpecimenInfoTable_type_label,
            Messages.SpecimenInfoTable_position_label,
            Messages.NewSpecimenInfoTable_source_worksheet_label,
            Messages.SpecimenInfoTable_created_label,
            Messages.SpecimenInfoTable_quantity_ml_label,
            Messages.SpecimenInfoTable_status_label,
            Messages.SpecimenInfoTable_origin_center_label,
            Messages.SpecimenInfoTable_current_center_label,
            Messages.SpecimenInfoTable_comments_label }) {
            @Override
            public String getColumnValue(SpecimenInfo row, int columnIndex) {
                switch (columnIndex) {
                case 0:
                    return row.specimen.getInventoryId();
                case 1:
                    return row.specimen.getSpecimenType().getNameShort();
                case 2:
                    return row.getPositionString(true, true);
                case 3:
                    return row.specimen.getParentSpecimen()
                        .getProcessingEvent().getWorksheet();
                case 4:
                    return DateFormatter.formatAsDateTime(row.specimen
                        .getCreatedAt());
                case 5:
                    return NumberFormatter.format(row.specimen.getQuantity());
                case 6:
                    return row.specimen.getActivityStatus().getName();
                case 7:
                    return row.specimen.getOriginInfo().getCenter()
                        .getNameShort();
                case 8:
                    return row.specimen.getCurrentCenter().getNameShort();
                case 9:
                    return getCommentLetter(row);
                default:
                    return ""; //$NON-NLS-1$
                }
            }

            @Override
            public Image getColumnImage(SpecimenInfo row, int columnIndex) {
                // FIXME retrieve this info as an enum in SpecimenInfo ?
                if (columnIndex == 5
                    && ActivityStatus.FLAGGED == row.specimen
                        .getActivityStatus())
                    return BgcPlugin.getDefault().getImageRegistry()
                        .get(BgcPlugin.IMG_ERROR);
                return null;
            }
        };

        private String[] headings;

        private ColumnsShown(String[] headings) {
            this.headings = headings;
        }

        public String[] getheadings() {
            return headings;
        }

        public abstract String getColumnValue(SpecimenInfo row, int columnIndex);

        public abstract Image getColumnImage(SpecimenInfo row, int columnIndex);
    }

    private ColumnsShown currentColumnsShowns;

    public NewSpecimenInfoTable(Composite parent,
        List<SpecimenInfo> specimenCollection, ColumnsShown columnsShown,
        int rowsPerPage) {
        super(parent, specimenCollection, columnsShown.getheadings(),
            rowsPerPage, SpecimenInfo.class);
        this.currentColumnsShowns = columnsShown;
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @Override
            public Image getColumnImage(Object element, int columnIndex) {
                SpecimenInfo info =
                    (SpecimenInfo) ((BiobankCollectionModel) element).o;
                if (info == null) {
                    return null;
                }
                return currentColumnsShowns.getColumnImage(info, columnIndex);
            }

            @Override
            public String getColumnText(Object element, int columnIndex) {
                SpecimenInfo info =
                    (SpecimenInfo) ((BiobankCollectionModel) element).o;
                if (info == null) {
                    if (columnIndex == 0) {
                        return Messages.infotable_loading_msg;
                    }
                    return ""; //$NON-NLS-1$
                }
                return currentColumnsShowns.getColumnValue(info, columnIndex);
            }
        };
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null) return null;
        SpecimenInfo r = (SpecimenInfo) o;
        return r.toString();
    }

    // FIXME do we need this method?
    // public void setSelection(SpecimenWrapper selectedSample) {
    // if (selectedSample == null)
    // return;
    // for (BiobankCollectionModel item : model) {
    // TableRowData info = (TableRowData) item.o;
    // if (info.specimen == selectedSample) {
    // getTableViewer().setSelection(new StructuredSelection(item),
    // true);
    // }
    // }
    // }

    @Override
    public SpecimenInfo getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null) return null;
        SpecimenInfo row = (SpecimenInfo) item.o;
        Assert.isNotNull(row);
        return row;
    }

    @Override
    public void editItem() {
        if (useDefaultEditItem) {
            // default edit item which opens the entry form for the selected
            // model object
            SpecimenInfo specimenInfo = getSelection();
            if (specimenInfo != null) {
                SpecimenWrapper specimen =
                    new SpecimenWrapper(SessionManager.getAppService(),
                        specimenInfo.specimen);
                try {
                    // specimen.reload();
                } catch (Exception e) {
                }
                AbstractAdapterBase adapter =
                    AdapterFactory.getAdapter(specimen);
                adapter.openEntryForm();
            }
            return;
        }

        super.editItem();
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return new BiobankTableSorter() {
            private static final long serialVersionUID = 1L;

            @Override
            public int compare(Object o1, Object o2) {
                if (o1 instanceof SpecimenInfo && o2 instanceof SpecimenInfo) {
                    SpecimenInfo s1 = (SpecimenInfo) o1;
                    SpecimenInfo s2 = (SpecimenInfo) o2;
                    return s1.specimen.getInventoryId().compareTo(
                        s2.specimen.getInventoryId());
                }
                return super.compare(01, o2);
            }
        };
    }

    private static String getCommentLetter(SpecimenInfo si) {
        return (si.specimen.getCommentCollection() == null || si.specimen
            .getCommentCollection().size() == 0) ? Messages.SpecimenInfoTable_no_first_letter
            : Messages.SpecimenInfoTable_yes_first_letter;
    }
}
