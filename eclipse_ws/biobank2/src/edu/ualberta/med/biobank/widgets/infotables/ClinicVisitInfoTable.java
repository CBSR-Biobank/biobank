package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.formatters.NumberFormatter;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class ClinicVisitInfoTable extends
    InfoTableWidget<CollectionEventWrapper> {

    private static class TableRowData {
        public Integer visit;
        public Long numSource;
        public Long numSpecimens;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { visit.toString(),
                numSource.toString(), numSpecimens.toString() });
        }
    }

    private static final String[] HEADINGS = new String[] {
        Messages.ClinicVisitInfoTable_nber_label,
        Messages.ClinicVisitInfoTable_source_specs_label,
        Messages.ClinicVisitInfoTable_aliquoted_spec_label };

    public ClinicVisitInfoTable(Composite parent,
        List<CollectionEventWrapper> collection) {
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
                        return Messages.ClinicVisitInfoTable_loading;
                    }
                    return ""; //$NON-NLS-1$
                }
                switch (columnIndex) {
                case 0:
                    return item.visit.toString();
                case 1:
                    return NumberFormatter.format(item.numSource);
                case 2:
                    return NumberFormatter.format(item.numSpecimens);
                default:
                    return ""; //$NON-NLS-1$
                }
            }
        };
    }

    @Override
    public Object getCollectionModelObject(CollectionEventWrapper p)
        throws Exception {
        TableRowData info = new TableRowData();
        info.visit = p.getVisitNumber();
        info.numSource = p.getSourceSpecimensCount(true);
        info.numSpecimens = p.getAliquotedSpecimensCount(true);
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
