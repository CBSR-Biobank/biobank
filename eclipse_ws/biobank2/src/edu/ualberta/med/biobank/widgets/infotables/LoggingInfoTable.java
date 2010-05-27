package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class LoggingInfoTable extends InfoTableWidget<Object> {

    public LoggingInfoTable(Composite parent, List<Object> collection,
        String[] headings, int[] columnWidths, int rowsPerPage) {
        super(parent, collection, headings, columnWidths, rowsPerPage);
    }

    class TableRowData {
        PatientWrapper patient;
        String pnumber;
        String studyNameShort;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { pnumber, studyNameShort },
                "\t");
        }
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
    protected BiobankTableSorter getTableSorter() {
        return null;
    }

    // XXX getCollectionModelObject
    @Override
    public Object getCollectionModelObject(Object o) throws Exception {
        return null;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }

    // XXX getCollection
    @Override
    public List<Object> getCollection() {
        return null;
    }

    // XXX getSelection
    @Override
    public Object getSelection() {
        return null;
    }
}