package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class CeListInfoTable extends InfoTableWidget<CollectionEventWrapper> {

    private static final int PAGE_SIZE_ROWS = 24;

    protected class TableRowData {
        CollectionEventWrapper pv;
        public String pnumber;
        public String studyNameShort;
        public Integer numSVs;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { pnumber, studyNameShort,
                numSVs.toString() }, "\t");
        }
    }

    private static final String[] HEADINGS = new String[] { "Patient Number",
        "Study", "Waybill", "Departed", "Clinic", "Source Vessels", "Specimens" };

    public CeListInfoTable(Composite parent, List<CollectionEventWrapper> pvs) {
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
                    return item.pnumber;
                case 1:
                    return item.studyNameShort;
                case 2:
                    return item.numSVs.toString();
                default:
                    return "";
                }
            }
        };
    }

    @Override
    public TableRowData getCollectionModelObject(CollectionEventWrapper cEvent)
        throws Exception {
        TableRowData info = new TableRowData();
        info.pv = cEvent;
        info.pnumber = cEvent.getPatient().getPnumber();
        StudyWrapper study = cEvent.getPatient().getStudy();
        if (study != null) {
            info.studyNameShort = study.getNameShort();
        } else {
            info.studyNameShort = new String();
        }
        info.numSVs = -1; // cEvent.getSpecimenCollection(false).size();
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }

    @Override
    public CollectionEventWrapper getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        TableRowData row = (TableRowData) item.o;
        if (row != null) {
            return row.pv;
        }
        return null;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }
}
