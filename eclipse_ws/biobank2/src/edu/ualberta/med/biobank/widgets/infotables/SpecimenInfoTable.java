package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class SpecimenInfoTable extends InfoTableWidget<SpecimenWrapper> {

    public static enum ColumnsShown {
        ALL(new String[] { "Inventory ID", "Type", "Patient", "Visit#",
            "Origin Center", "Current Center", "Position", "Time created",
            "Quantity (ml)", "Activity status", "Comment" }) {
            @Override
            public String getColumnValue(TableRowData row, int columnIndex) {
                switch (columnIndex) {
                case 0:
                    return row.inventoryId;
                case 1:
                    return row.type;
                case 2:
                    return row.patient;
                case 3:
                    return row.pvNumber.toString();
                case 4:
                    return row.originCenter;
                case 5:
                    return row.center;
                case 6:
                    return row.position;
                case 7:
                    return row.createdAt;
                case 8:
                    return row.quantity;
                case 9:
                    return row.activityStatus;
                case 10:
                    return row.comment;
                default:
                    return "";
                }
            }

            @Override
            public Image getColumnImage(TableRowData row, int columnIndex) {
                if (columnIndex == 9
                    && ActivityStatusWrapper.FLAGGED_STATUS_STRING
                        .equals(row.activityStatus))
                    return BgcPlugin.getDefault().getImageRegistry()
                        .get(BgcPlugin.IMG_ERROR);
                return null;
            }
        },
        SOURCE_SPECIMENS(new String[] { "Inventory ID", "Type", "Position",
            "Time drawn", "Quantity (ml)", "Activity status", "Study",
            "Patient #", "Origin Center", "Current Center", "Comment" }) {
            @Override
            public String getColumnValue(TableRowData row, int columnIndex) {
                switch (columnIndex) {
                case 0:
                    return row.inventoryId;
                case 1:
                    return row.type;
                case 2:
                    return row.position;
                case 3:
                    return row.createdAt;
                case 4:
                    return row.quantity;
                case 5:
                    return row.activityStatus;
                case 6:
                    return row.studyName;
                case 7:
                    return row.patient;
                case 8:
                    return row.originCenter;
                case 9:
                    return row.center;
                case 10:
                    return (row.comment == null || row.comment.equals("")) ? "N"
                        : "Y";
                default:
                    return "";
                }
            }

            @Override
            public Image getColumnImage(TableRowData row, int columnIndex) {
                if (columnIndex == 5
                    && ActivityStatusWrapper.FLAGGED_STATUS_STRING
                        .equals(row.activityStatus))
                    return BgcPlugin.getDefault().getImageRegistry()
                        .get(BgcPlugin.IMG_ERROR);
                return null;
            }
        },
        ALIQUOTS(new String[] { "Inventory ID", "Type", "Position",
            "Time created", "Worksheet", "Quantity (ml)", "Activity status",
            "Origin Center", "Current Center", "Comment" }) {
            @Override
            public String getColumnValue(TableRowData row, int columnIndex) {
                switch (columnIndex) {
                case 0:
                    return row.inventoryId;
                case 1:
                    return row.type;
                case 2:
                    return row.position;
                case 3:
                    return row.createdAt;
                case 4:
                    return row.worksheet;
                case 5:
                    return row.quantity;
                case 6:
                    return row.activityStatus;
                case 7:
                    return row.originCenter;
                case 8:
                    return row.center;
                case 9:
                    return (row.comment == null || row.comment.equals("")) ? "N"
                        : "Y";
                default:
                    return "";
                }
            }

            @Override
            public Image getColumnImage(TableRowData row, int columnIndex) {
                if (columnIndex == 6
                    && ActivityStatusWrapper.FLAGGED_STATUS_STRING
                        .equals(row.activityStatus))
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

        public abstract String getColumnValue(TableRowData row, int columnIndex);

        public abstract Image getColumnImage(TableRowData row, int columnIndex);
    }

    protected static class TableRowData {
        public String worksheet;
        public SpecimenWrapper specimen;
        public String inventoryId;
        public String type;
        public String patient;
        public String pvNumber;
        public String studyName;
        public String createdAt;
        public String center;
        public String originCenter;
        public String quantity;
        public String position;
        public String activityStatus;
        public String comment;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { inventoryId, type, patient,
                pvNumber, createdAt, center, originCenter, quantity, position,
                activityStatus, comment }, "\t");
        }
    }

    private ColumnsShown currentColumnsShowns;

    public SpecimenInfoTable(Composite parent,
        List<SpecimenWrapper> specimenCollection, ColumnsShown columnsShown,
        int rowsPerPage) {
        super(parent, specimenCollection, columnsShown.getheadings(),
            rowsPerPage, SpecimenWrapper.class);
        this.currentColumnsShowns = columnsShown;
    }

    @Override
    protected BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public Image getColumnImage(Object element, int columnIndex) {
                TableRowData info = (TableRowData) ((BiobankCollectionModel) element).o;
                if (info == null) {
                    return null;
                }
                return currentColumnsShowns.getColumnImage(info, columnIndex);
            }

            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData info = (TableRowData) ((BiobankCollectionModel) element).o;
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
    public TableRowData getCollectionModelObject(SpecimenWrapper specimen)
        throws Exception {
        TableRowData info = new TableRowData();
        info.specimen = specimen;
        info.inventoryId = specimen.getInventoryId();
        SpecimenTypeWrapper type = specimen.getSpecimenType();
        Assert.isNotNull(type, "specimen with null for specimen type");
        info.type = type.getName();
        CollectionEventWrapper cEvent = specimen.getCollectionEvent();

        info.patient = "";
        info.studyName = "";
        info.pvNumber = "";

        if (cEvent != null) {
            PatientWrapper patient = cEvent.getPatient();

            if (patient != null) {
                info.patient = patient.getPnumber();
                StudyWrapper study = patient.getStudy();
                if (study != null) {
                    info.studyName = study.getNameShort();
                }
            }

            Integer visitNumber = cEvent.getVisitNumber();
            info.pvNumber = (visitNumber == null) ? "" : visitNumber.toString();
        }

        info.createdAt = specimen.getFormattedCreatedAt();
        info.worksheet = specimen.getParentSpecimen() == null ? "" : specimen
            .getParentSpecimen().getProcessingEvent().getWorksheet();
        Double quantity = specimen.getQuantity();
        info.quantity = (quantity == null) ? "" : quantity.toString();
        info.position = specimen.getPositionString();
        ActivityStatusWrapper status = specimen.getActivityStatus();
        info.activityStatus = (status == null) ? "" : status.getName();
        info.comment = specimen.getComment();
        info.center = specimen.getCurrentCenter().getNameShort();

        info.originCenter = "";
        OriginInfoWrapper oi = specimen.getOriginInfo();
        if (oi != null) {
            CenterWrapper<?> originCenter = oi.getCenter();
            if (originCenter != null)
                info.originCenter = originCenter.getNameShort();
        }

        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        TableRowData r = (TableRowData) o;
        return r.toString();
    }

    public void setSelection(SpecimenWrapper selectedSample) {
        if (selectedSample == null)
            return;
        for (BiobankCollectionModel item : model) {
            TableRowData info = (TableRowData) item.o;
            if (info.specimen == selectedSample) {
                getTableViewer().setSelection(new StructuredSelection(item),
                    true);
            }
        }
    }

    @Override
    public SpecimenWrapper getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        TableRowData row = (TableRowData) item.o;
        Assert.isNotNull(row);
        return row.specimen;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }
}
