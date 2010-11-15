package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class PatientInfoTable extends InfoTableWidget<PatientWrapper> {

    private static final int PAGE_SIZE_ROWS = 5;

    protected class TableRowData {
        PatientWrapper patient;
        public String pnumber;
        public String studyNameShort;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { pnumber, studyNameShort },
                "\t");
        }
    }

    private static final String[] HEADINGS = new String[] { "Patient Number",
        "Study" };

    public PatientInfoTable(Composite parent, List<PatientWrapper> patients) {
        super(parent, patients, HEADINGS, PAGE_SIZE_ROWS);
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
                case 3:
                default:
                    return "";
                }
            }
        };
    }

    @Override
    public TableRowData getCollectionModelObject(PatientWrapper patient)
        throws Exception {
        TableRowData info = new TableRowData();
        info.patient = patient;
        info.pnumber = patient.getPnumber();
        StudyWrapper study = patient.getStudy();
        if (study != null) {
            info.studyNameShort = study.getNameShort();
        } else {
            info.studyNameShort = new String();
        }
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }

    @Override
    public PatientWrapper getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        TableRowData row = (TableRowData) item.o;
        Assert.isNotNull(row);
        return row.patient;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }
}
