package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class ClinicVisitInfoTable extends InfoTableWidget<ProcessingEventWrapper> {

    class TableRowData {
        public String clinicName;
        public String visit;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { clinicName, visit });
        }
    }

    private static final String[] HEADINGS = new String[] { "Clinic",
        "Patient Visits (Date Processed)" };

    public ClinicVisitInfoTable(Composite parent,
        List<ProcessingEventWrapper> collection) {
        super(parent, collection, HEADINGS);
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
                    return item.clinicName;
                case 1:
                    return item.visit;
                default:
                    return "";
                }
            }
        };
    }

    @Override
    public Object getCollectionModelObject(ProcessingEventWrapper p)
        throws Exception {
        TableRowData info = new TableRowData();
        info.clinicName = p.getCollectionEvent().getClinic().getNameShort();
        info.visit = p.getFormattedDateProcessed();
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }

    /*
     * @Override public void setSelection(ClinicWrapper item) {
     * BiobankCollectionModel modelItem = null; for (BiobankCollectionModel m :
     * model) { if (item.equals(m.o)) { modelItem = m; break; } } if (modelItem
     * == null) return;
     * 
     * tableViewer.setSelection(new StructuredSelection(modelItem)); }
     */
}
