package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class SpecimenInfoTable extends InfoTableWidget<SpecimenWrapper> {

    private static final int PAGE_SIZE_ROWS = 5;

    protected class TableRowData {
        public String pnumber;
        public SpecimenWrapper spec;
        public Integer pv;
        public String type;

        @Override
        public String toString() {
            return StringUtils.join(
                new String[] { pnumber, pv.toString(), type }, "\t");
        }
    }

    private static final String[] HEADINGS = new String[] { "Patient Number",
        "Study" };

    public SpecimenInfoTable(Composite parent, List<SpecimenWrapper> collection) {
        super(parent, collection, HEADINGS, PAGE_SIZE_ROWS);
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
                    return item.pv.toString();
                case 2:
                    return item.type;
                default:
                    return "";
                }
            }
        };
    }

    @Override
    public TableRowData getCollectionModelObject(SpecimenWrapper spec)
        throws Exception {
        TableRowData info = new TableRowData();
        info.spec = spec;
        info.pnumber = spec.getCollectionEvent().getPatient().getPnumber();
        info.pv = spec.getCollectionEvent().getVisitNumber();
        info.type = spec.getSpecimenType().getName();
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }

    @Override
    public SpecimenWrapper getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        TableRowData row = (TableRowData) item.o;
        Assert.isNotNull(row);
        return row.spec;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }
}
