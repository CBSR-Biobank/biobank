package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class PeListInfoTable extends InfoTableWidget<ProcessingEventWrapper> {

    private static final int PAGE_SIZE_ROWS = 24;

    protected class TableRowData {
        ProcessingEventWrapper pe;
        public String studyNameShort;
        public Long numSVs;
        public Long numAliquots;

        @Override
        public String toString() {
            return StringUtils.join(
                new String[] { studyNameShort, numSVs.toString(),
                    numAliquots.toString() }, "\t");
        }
    }

    private static final String[] HEADINGS = new String[] { "Study",
        "Source Specimens", "Aliquoted Specimens" };

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
                        return "loading...";
                    }
                    return "";
                }
                switch (columnIndex) {
                case 0:
                    return item.studyNameShort;
                case 1:
                    return item.numSVs.toString();
                case 2:
                    return item.numAliquots.toString();
                default:
                    return "";
                }
            }
        };
    }

    @Override
    public TableRowData getCollectionModelObject(ProcessingEventWrapper pEvent)
        throws Exception {
        TableRowData info = new TableRowData();
        info.pe = pEvent;
        StudyWrapper study = pEvent.getSpecimenCollection(false).get(0)
            .getCollectionEvent().getPatient().getStudy();
        if (study != null) {
            info.studyNameShort = study.getNameShort();
        } else {
            info.studyNameShort = new String();
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
