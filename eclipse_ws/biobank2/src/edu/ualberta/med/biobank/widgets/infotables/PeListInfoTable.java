package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Composite;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.processingEvent.ProcessingEventBriefInfo;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.formatters.NumberFormatter;
import edu.ualberta.med.biobank.common.permission.processingEvent.ProcessingEventDeletePermission;
import edu.ualberta.med.biobank.common.permission.processingEvent.ProcessingEventReadPermission;
import edu.ualberta.med.biobank.common.permission.processingEvent.ProcessingEventUpdatePermission;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.gui.common.widgets.AbstractInfoTableWidget;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class PeListInfoTable extends InfoTableWidget<ProcessingEventBriefInfo> {
    public static final I18n i18n = I18nFactory
        .getI18n(PeListInfoTable.class);

    private static final int PAGE_SIZE_ROWS = 24;

    protected class TableRowData {
        ProcessingEventBriefInfo pe;
        public String startDate;
        public String studyNameShort;
        public Long numSVs;
        public Long numAliquots;

        @SuppressWarnings("nls")
        @Override
        public String toString() {
            return StringUtils.join(new String[] { startDate, studyNameShort,
                numSVs.toString(), numAliquots.toString() }, "\t");
        }
    }

    @SuppressWarnings("nls")
    private static final String[] HEADINGS = new String[] {
        i18n.tr("Start date"),
        Study.NAME.singular().toString(),
        SourceSpecimen.NAME.plural().toString(),
        AliquotedSpecimen.NAME.plural().toString() };

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
                        return AbstractInfoTableWidget.LOADING;
                    }
                    return StringUtil.EMPTY_STRING;
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
                    return StringUtil.EMPTY_STRING;
                }
            }
        };
    }

    @Override
    public TableRowData getCollectionModelObject(Object o) throws Exception {
        TableRowData info = new TableRowData();
        info.pe = (ProcessingEventBriefInfo) o;
        info.startDate =
            DateFormatter.formatAsDateTime(info.pe.pevent.getCreatedAt());
        info.studyNameShort = info.pe.studyNameShort;
        info.numSVs = info.pe.sourceSpcCount;
        info.numAliquots = info.pe.aliquotSpcCount;
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
            new ProcessingEventUpdatePermission(target.pevent));
    }

    @Override
    protected Boolean canDelete(ProcessingEventBriefInfo target)
        throws ApplicationException {
        return SessionManager.getAppService().isAllowed(
            new ProcessingEventDeletePermission(target.pevent));
    }

    @Override
    protected Boolean canView(ProcessingEventBriefInfo target)
        throws ApplicationException {
        return SessionManager.getAppService().isAllowed(
            new ProcessingEventReadPermission(target.pevent));
    }
}
