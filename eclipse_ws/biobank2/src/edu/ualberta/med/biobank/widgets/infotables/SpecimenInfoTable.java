package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.formatters.NumberFormatter;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class SpecimenInfoTable extends InfoTableWidget<SpecimenWrapper> {

    public static enum ColumnsShown {
        ALL(new String[] { Messages.SpecimenInfoTable_inventoryid_label,
            Messages.SpecimenInfoTable_type_label,
            Messages.SpecimenInfoTable_patient_label,
            Messages.SpecimenInfoTable_visitNber_label,
            Messages.SpecimenInfoTable_current_center_label,
            Messages.SpecimenInfoTable_position_label,
            Messages.SpecimenInfoTable_created_label,
            Messages.SpecimenInfoTable_quantity_label,
            Messages.SpecimenInfoTable_status_label,
            Messages.SpecimenInfoTable_comment_label }) {
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
                    return row.pvNumber;
                case 4:
                    return row.center;
                case 5:
                    return row.position;
                case 6:
                    return row.createdAt;
                case 7:
                    return NumberFormatter.format(row.quantity);
                case 8:
                    return row.activityStatus;
                case 9:
                    return row.comment;
                default:
                    return ""; //$NON-NLS-1$
                }
            }
        },
        SOURCE_SPECIMENS(new String[] {
            Messages.SpecimenInfoTable_inventoryid_label,
            Messages.SpecimenInfoTable_type_label,
            Messages.SpecimenInfoTable_time_drawn_label,
            Messages.SpecimenInfoTable_quantity_label,
            Messages.SpecimenInfoTable_status_label,
            Messages.SpecimenInfoTable_study_label,
            Messages.SpecimenInfoTable_pnumber_label,
            Messages.SpecimenInfoTable_current_center_label }) {
            @Override
            public String getColumnValue(TableRowData row, int columnIndex) {
                switch (columnIndex) {
                case 0:
                    return row.inventoryId;
                case 1:
                    return row.type;
                case 2:
                    return row.createdAt;
                case 3:
                    return NumberFormatter.format(row.quantity);
                case 4:
                    return row.activityStatus;
                case 5:
                    return row.studyName;
                case 6:
                    return row.patient;
                case 7:
                    return row.center;
                default:
                    return ""; //$NON-NLS-1$
                }
            }
        },
        ALIQUOTS(new String[] { Messages.SpecimenInfoTable_inventoryid_label,
            Messages.SpecimenInfoTable_type_label,
            Messages.SpecimenInfoTable_created_label,
            Messages.SpecimenInfoTable_quantity_label,
            Messages.SpecimenInfoTable_status_label,
            Messages.SpecimenInfoTable_study_label,
            Messages.SpecimenInfoTable_pnumber_label,
            Messages.SpecimenInfoTable_current_center_label }) {
            @Override
            public String getColumnValue(TableRowData row, int columnIndex) {
                switch (columnIndex) {
                case 0:
                    return row.inventoryId;
                case 1:
                    return row.type;
                case 2:
                    return row.createdAt;
                case 3:
                    return NumberFormatter.format(row.quantity);
                case 4:
                    return row.activityStatus;
                case 5:
                    return row.studyName;
                case 6:
                    return row.patient;
                case 7:
                    return row.center;
                default:
                    return ""; //$NON-NLS-1$
                }
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
    }

    protected static class TableRowData {
        public SpecimenWrapper specimen;
        public String inventoryId;
        public String type;
        public String patient;
        public String pvNumber;
        public String studyName;
        public String createdAt;
        public String center;
        public Double quantity;
        public String position;
        public String activityStatus;
        public String comment;

        @Override
        public String toString() {
            return StringUtils.join(
                new String[] { inventoryId, type, patient, pvNumber, createdAt,
                    center, (quantity == null) ? "" : quantity.toString(),
                    position, activityStatus, comment }, "\t"); //$NON-NLS-1$
        }
    }

    private ColumnsShown currentColumnsShowns;

    public SpecimenInfoTable(Composite parent,
        List<SpecimenWrapper> specimenCollection, ColumnsShown columnsShown,
        int rowsPerPage) {
        super(parent, specimenCollection, columnsShown.getheadings(),
            rowsPerPage);
        this.currentColumnsShowns = columnsShown;
    }

    @Override
    protected BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData info = (TableRowData) ((BiobankCollectionModel) element).o;
                if (info == null) {
                    if (columnIndex == 0) {
                        return Messages.SpecimenInfoTable_loading;
                    }
                    return ""; //$NON-NLS-1$
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
        Assert.isNotNull(type, "specimen with null for specimen type"); //$NON-NLS-1$
        info.type = type.getName();
        CollectionEventWrapper cEvent = specimen.getCollectionEvent();

        info.patient = ""; //$NON-NLS-1$
        info.studyName = ""; //$NON-NLS-1$
        info.pvNumber = ""; //$NON-NLS-1$

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
            info.pvNumber = (visitNumber == null) ? "" : visitNumber.toString(); //$NON-NLS-1$
        }

        info.createdAt = specimen.getFormattedCreatedAt();
        Double quantity = specimen.getQuantity();
        info.quantity = quantity;
        info.position = specimen.getPositionString();
        ActivityStatusWrapper status = specimen.getActivityStatus();
        info.activityStatus = (status == null) ? "" : status.getName(); //$NON-NLS-1$
        info.comment = specimen.getComment();
        info.center = specimen.getCurrentCenter().getNameShort();

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
