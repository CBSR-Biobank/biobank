package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.formatters.NumberFormatter;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class PeListInfoTable extends InfoTableWidget<ProcessingEventWrapper> {

    private static final int PAGE_SIZE_ROWS = 24;

    protected class TableRowData {
        ProcessingEventWrapper pe;
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

    public PeListInfoTable(Composite parent, List<ProcessingEventWrapper> pvs) {
        super(parent, pvs, HEADINGS, PAGE_SIZE_ROWS);
    }

    @Override
    protected BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData item = (TableRowData) ((BiobankCollectionModel) element).o;
                if (item == null) {
                    if (columnIndex == 0) {
                        return Messages.PeListInfoTable_loading;
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
    public TableRowData getCollectionModelObject(ProcessingEventWrapper pEvent)
        throws Exception {
        TableRowData info = new TableRowData();
        info.pe = pEvent;
        info.startDate = pEvent.getFormattedCreatedAt();
        StudyWrapper study = pEvent.getSpecimenCollection(false).get(0)
            .getCollectionEvent().getPatient().getStudy();
        if (study != null) {
            info.studyNameShort = study.getNameShort();
        } else {
            info.studyNameShort = ""; //$NON-NLS-1$
        }
        info.numSVs = pEvent.getSpecimenCount(false);
        info.numAliquots = pEvent.getChildSpecimenCount();
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }

    @Override
    public ProcessingEventWrapper getSelection() {
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
}
