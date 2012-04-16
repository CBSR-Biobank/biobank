package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.processingEvent.ProcessingEventBriefInfo;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.formatters.NumberFormatter;
import edu.ualberta.med.biobank.common.permission.processingEvent.ProcessingEventDeletePermission;
import edu.ualberta.med.biobank.common.permission.processingEvent.ProcessingEventUpdatePermission;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class PeListInfoTable extends InfoTableWidget<ProcessingEventBriefInfo> {

    private static final int PAGE_SIZE_ROWS = 24;

    protected class TableRowData {
        ProcessingEventBriefInfo pe;
        public String startDate;
        public String studyNameShort;
        public Long numSVs;
        public Long numAliquots;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { startDate, studyNameShort,
                numSVs.toString(), numAliquots.toString() }, "\t"); //$NON-NLS-1$
        }
    }

    private static final String[] HEADINGS = new String[] {
        Messages.PeListInfoTable_start_label,
        Messages.PeListInfoTable_study_label,
        Messages.PeListInfoTable_sources_label,
        Messages.PeListInfoTable_aliquoteds_label };

    public PeListInfoTable(Composite parent, List<ProcessingEventBriefInfo> pvs) {
        super(parent, pvs, HEADINGS, PAGE_SIZE_ROWS,
            ProcessingEventBriefInfo.class);
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData item =
                    (TableRowData) ((BiobankCollectionModel) element).o;
                if (item == null) {
                    if (columnIndex == 0) {
                        return Messages.infotable_loading_msg;
                    }
                    return ""; //$NON-NLS-1$
                }
                switch (columnIndex) {
                case 0:
                    return item.startDate;
                case 1:
                    return item.studyNameShort;
                case 2:
                    return NumberFormatter.format(item.numSVs);
                case 3:
                    return NumberFormatter.format(item.numAliquots);
                default:
                    return ""; //$NON-NLS-1$
                }
            }
        };
    }

    @Override
    public TableRowData getCollectionModelObject(Object o) throws Exception {
        TableRowData info = new TableRowData();
        info.pe = (ProcessingEventBriefInfo) o;
        info.startDate =
            DateFormatter.formatAsDateTime(info.pe.e.getCreatedAt());
        info.studyNameShort = info.pe.study;
        info.numSVs = info.pe.svs;
        info.numAliquots = info.pe.aliquots;
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }

    @Override
    public ProcessingEventBriefInfo getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        TableRowData row = (TableRowData) item.o;
        if (row != null) {
            return row.pe;
        }
        return null;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }

    @Override
    protected Boolean canEdit(ProcessingEventBriefInfo target)
        throws ApplicationException {
        return SessionManager.getAppService().isAllowed(
            new ProcessingEventUpdatePermission(target.e.getId()));
    }

    @Override
    protected Boolean canDelete(ProcessingEventBriefInfo target)
        throws ApplicationException {
        return SessionManager.getAppService().isAllowed(
            new ProcessingEventDeletePermission(target.e.getId()));
    }
}
