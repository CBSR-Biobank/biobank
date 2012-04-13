package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.action.patient.PatientGetCollectionEventInfosAction.PatientCEventInfo;
import edu.ualberta.med.biobank.common.formatters.NumberFormatter;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ClinicVisitInfoTable extends
    InfoTableWidget<PatientCEventInfo> {

    private static class TableRowData {
        public PatientCEventInfo cevent;

        @Override
        public String toString() {
            return StringUtils.join(new String[] {
                cevent.cevent.getVisitNumber().toString(),
                cevent.sourceSpecimenCount.toString(),
                cevent.aliquotedSpecimenCount.toString() });
        }
    }

    private static final String[] HEADINGS = new String[] {
        Messages.ClinicVisitInfoTable_nber_label,
        Messages.ClinicVisitInfoTable_source_specs_label,
        Messages.ClinicVisitInfoTable_aliquoted_spec_label };

    public ClinicVisitInfoTable(Composite parent,
        List<PatientCEventInfo> collection) {
        super(parent, collection, HEADINGS, CollectionEventWrapper.class);
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
                    return item.cevent.cevent.getVisitNumber().toString();
                case 1:
                    return NumberFormatter
                        .format(item.cevent.sourceSpecimenCount);
                case 2:
                    return NumberFormatter
                        .format(item.cevent.aliquotedSpecimenCount);
                default:
                    return ""; //$NON-NLS-1$
                }
            }
        };
    }

    @Override
    public BiobankTableSorter getComparator() {
        return new BiobankTableSorter() {
            private static final long serialVersionUID = 1L;

            @Override
            public int compare(Object e1, Object e2) {
                try {
                    PatientCEventInfo i1 = (PatientCEventInfo) e1;
                    PatientCEventInfo i2 = (PatientCEventInfo) e2;
                    return super.compare(i1.cevent.getVisitNumber(),
                        i2.cevent.getVisitNumber());
                } catch (Exception e) {
                    return 0;
                }
            }
        };
    }

    @Override
    public Object getCollectionModelObject(Object o) throws Exception {
        TableRowData info = new TableRowData();
        info.cevent = (PatientCEventInfo) o;
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }

    @Override
    public PatientCEventInfo getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        return ((TableRowData) item.o).cevent;
    }

    @Override
    protected Boolean canEdit(PatientCEventInfo target)
        throws ApplicationException {
        return false;
    }

    @Override
    protected Boolean canDelete(PatientCEventInfo target)
        throws ApplicationException {
        return false;
    }

}
