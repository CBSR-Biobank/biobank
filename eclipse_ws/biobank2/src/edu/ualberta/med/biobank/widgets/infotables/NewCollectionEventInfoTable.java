package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.action.collectionEvent.GetPatientCollectionEventInfosAction.PatientCEventInfo;
import edu.ualberta.med.biobank.common.formatters.NumberFormatter;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.model.CollectionEvent;

public class NewCollectionEventInfoTable extends InfoTableWidget {

    private static final String[] HEADINGS = new String[] {
        Messages.CollectionEventInfoTable_header_visitNumber,
        Messages.CollectionEventInfoTable_header_numSourceSpecimens,
        Messages.CollectionEventInfoTable_header_numAliquotedSpecimens,
        Messages.CollectionEventInfoTable_header_comment };

    public NewCollectionEventInfoTable(Composite parent,
        List<PatientCEventInfo> collection) {
        super(parent, collection, HEADINGS, 10, CollectionEvent.class);
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                PatientCEventInfo info = (PatientCEventInfo) ((BiobankCollectionModel) element).o;
                if (info == null) {
                    if (columnIndex == 0) {
                        return Messages.infotable_loading_msg;
                    }
                    return ""; //$NON-NLS-1$
                }
                switch (columnIndex) {
                case 0:
                    return info.cevent.getVisitNumber().toString();
                case 1:
                    return NumberFormatter.format(info.sourceSpecimenCount);
                case 2:
                    return NumberFormatter.format(info.aliquotedSpecimenCount);
                case 3:
                    return info.cevent.getCommentCollection().toString();

                default:
                    return ""; //$NON-NLS-1$
                }
            }
        };
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((PatientCEventInfo) o).toString();
    }

    @Override
    public CollectionEvent getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        PatientCEventInfo row = (PatientCEventInfo) item.o;
        Assert.isNotNull(row);
        return row.cevent;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return new BiobankTableSorter() {
            private static final long serialVersionUID = 1L;

            @Override
            public int compare(Object o1, Object o2) {
                if (o1 instanceof PatientCEventInfo
                    && o2 instanceof PatientCEventInfo) {
                    PatientCEventInfo p1 = (PatientCEventInfo) o1;
                    PatientCEventInfo p2 = (PatientCEventInfo) o2;
                    return p1.cevent.visitNumber
                        .compareTo(p2.cevent.visitNumber);
                }
                return super.compare(01, o2);
            }
        };
    }
}
