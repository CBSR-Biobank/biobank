package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.formatters.NumberFormatter;
import edu.ualberta.med.biobank.common.permission.specimen.SpecimenDeletePermission;
import edu.ualberta.med.biobank.common.permission.specimen.SpecimenReadPermission;
import edu.ualberta.med.biobank.common.permission.specimen.SpecimenUpdatePermission;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.model.AbstractPosition;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.HasCreatedAt;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class NewSpecimenInfoTable extends InfoTableWidget<SpecimenInfo> {

    public static enum ColumnsShown {
        PEVENT_SOURCE_SPECIMENS(new String[] {
            Specimen.PropertyName.INVENTORY_ID.toString(),
            "Type",
            AbstractPosition.NAME.singular().toString(),
            "Time drawn",
            "Quantity (ml)",
            ActivityStatus.NAME.singular().toString(),
            Study.NAME.singular().toString(),
            Patient.PropertyName.PNUMBER.toString(),
            "Origin Center",
            "Current Center",
            Comment.NAME.plural().toString() }) {
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
                    return oi == null ? "" : oi.getCenter().getNameShort();
                case 9:
                    return row.specimen.getCurrentCenter().getNameShort();
                case 10:
                    return row.comment;
                default:
                    return "";
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
            Specimen.PropertyName.INVENTORY_ID.toString(),
            "Type",
            AbstractPosition.NAME.singular().toString(),
            "Time drawn",
            AliquotedSpecimen.PropertyName.QUANTITY.toString(),
            ActivityStatus.NAME.singular().toString(),
            ProcessingEvent.PropertyName.WORKSHEET.toString(),
            "Origin Center",
            "Current Center",
            Comment.NAME.plural().toString() }) {
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
                    return pe == null ? "" : pe.getWorksheet();
                case 7:
                    OriginInfo oi = row.specimen.getOriginInfo();
                    return oi == null ? "" : oi.getCenter().getNameShort();
                case 8:
                    return row.specimen.getCurrentCenter().getNameShort();
                case 9:
                    return row.comment;
                default:
                    return "";
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
            Specimen.PropertyName.INVENTORY_ID.toString(),
            "Type",
            AbstractPosition.NAME.singular().toString(),
            "Source worksheet",
            HasCreatedAt.PropertyName.CREATED_AT.toString(),
            "Quantity (ml)",
            ActivityStatus.NAME.singular().toString(),
            "Origin Center",
            "Current Center",
            Comment.NAME.plural().toString() }) {
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
                    return row.comment;
                default:
                    return "";
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

    private final ColumnsShown currentColumnsShowns;

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
                        return "loading...";
                    }
                    return "";
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

    @Override
    public SpecimenInfo getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null) return null;
        SpecimenInfo row = (SpecimenInfo) item.o;
        Assert.isNotNull(row);
        return row;
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

    @Override
    protected Boolean canEdit(SpecimenInfo target) throws ApplicationException {
        return target != null && SessionManager.getAppService().isAllowed(
            new SpecimenUpdatePermission(target.specimen.getId()));
    }

    @Override
    protected Boolean canDelete(SpecimenInfo target)
        throws ApplicationException {
        return target != null && SessionManager.getAppService().isAllowed(
            new SpecimenDeletePermission(target.specimen.getId()));
    }

    @Override
    protected Boolean canView(SpecimenInfo target) throws ApplicationException {
        return target != null && SessionManager.getAppService().isAllowed(
            new SpecimenReadPermission(target.specimen.getId()));
    }

}
