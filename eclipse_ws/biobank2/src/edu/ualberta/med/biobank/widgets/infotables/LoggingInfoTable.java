package edu.ualberta.med.biobank.widgets.infotables;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

import edu.ualberta.med.biobank.common.wrappers.LogWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class LoggingInfoTable extends ReportTableWidget<LogWrapper> {

    private static final String[] HEADINGS = new String[] {
        Messages.LoggingInfoTable_site_label,
        Messages.LoggingInfoTable_user_label,
        Messages.LoggingInfoTable_date_label,
        Messages.LoggingInfoTable_action_label,
        Messages.LoggingInfoTable_type_label,
        Messages.LoggingInfoTable_pnumber_label,
        Messages.LoggingInfoTable_inventoryid_label,
        Messages.LoggingInfoTable_location_label,
        Messages.LoggingInfoTable_details_label };

    private static final int PAGE_SIZE_ROWS = 20;

    public LoggingInfoTable(Composite parent, List<LogWrapper> collection) {
        super(parent, collection, HEADINGS, PAGE_SIZE_ROWS);
    }

    private static class TableRowData {
        String center;
        String user;
        String date;
        String action;
        String type;
        String patientNumber;
        String inventoryId;
        String positionLabel;
        String details;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { center, user, date, action,
                type, patientNumber, inventoryId, positionLabel, details },
                "\t"); //$NON-NLS-1$
        }
    }

    @Override
    public BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData item = getCollectionModelObject((LogWrapper) element);
                if (item == null) {
                    if (columnIndex == 0) {
                        return Messages.LoggingInfoTable_loading;
                    }
                    return ""; //$NON-NLS-1$
                }
                switch (columnIndex) {
                case 0:
                    return item.center;
                case 1:
                    return item.user;
                case 2:
                    return item.date;
                case 3:
                    return item.action;
                case 4:
                    return item.type;
                case 5:
                    return item.patientNumber;
                case 6:
                    return item.inventoryId;
                case 7:
                    return item.positionLabel;
                case 8:
                    return item.details;
                default:
                    return ""; //$NON-NLS-1$
                }
            }
        };
    }

    public TableRowData getCollectionModelObject(LogWrapper logQuery) {
        TableRowData info = new TableRowData();
        info.center = logQuery.getCenter();
        info.user = logQuery.getUsername();
        info.action = logQuery.getAction();
        info.type = logQuery.getType();
        info.positionLabel = logQuery.getLocationLabel();
        info.patientNumber = logQuery.getPatientNumber();
        info.inventoryId = logQuery.getInventoryId();
        info.details = logQuery.getDetails();

        Date logQueryDate = logQuery.getCreatedAt();
        if (logQueryDate != null) {
            SimpleDateFormat dateTimeSecond = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$
            info.date = dateTimeSecond.format(logQueryDate);
        } else {
            info.date = null;
        }

        return info;
    }

    public Table getTable() {
        return tableViewer.getTable();
    }

    @Override
    public List<LogWrapper> getCollection() {
        return null;
    }

}