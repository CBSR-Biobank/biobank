package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class CollectionEventInfoTable extends
    InfoTableWidget<CollectionEventWrapper> {

    private static class TableRowData {
        CollectionEventWrapper collectionEvent;
        Integer visitNumber;
        long sourceSpecimenCount;
        long aliquotedSpecimenCount;
        String comment;

        @Override
        public String toString() {
            return StringUtils.join(
                new String[] { visitNumber.toString(),
                    String.valueOf(sourceSpecimenCount),
                    String.valueOf(aliquotedSpecimenCount), comment }, "\t"); //$NON-NLS-1$
        }
    }

    private static final String[] HEADINGS = new String[] {
        Messages.getString("CollectionEventInfoTable.header.visitNumber"), //$NON-NLS-1$
        Messages
            .getString("CollectionEventInfoTable.header.numSourceSpecimens"), //$NON-NLS-1$
        Messages
            .getString("CollectionEventInfoTable.header.numAliquotedSpecimens"), //$NON-NLS-1$
        Messages.getString("CollectionEventInfoTable.header.comment") }; //$NON-NLS-1$

    public CollectionEventInfoTable(Composite parent,
        List<CollectionEventWrapper> collection) {
        super(parent, collection, HEADINGS, 10);
    }

    @Override
    protected BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData info = (TableRowData) ((BiobankCollectionModel) element).o;
                if (info == null) {
                    if (columnIndex == 0) {
                        return Messages.getString("infotable.loading.msg"); //$NON-NLS-1$
                    }
                    return ""; //$NON-NLS-1$
                }
                switch (columnIndex) {
                case 0:
                    return info.visitNumber.toString();
                case 1:
                    return String.valueOf(info.sourceSpecimenCount);
                case 2:
                    return String.valueOf(info.aliquotedSpecimenCount);
                case 3:
                    return info.comment;

                default:
                    return ""; //$NON-NLS-1$
                }
            }
        };
    }

    @Override
    public Object getCollectionModelObject(
        CollectionEventWrapper collectionEvent) throws Exception {
        TableRowData info = new TableRowData();
        info.collectionEvent = collectionEvent;
        info.visitNumber = collectionEvent.getVisitNumber();
        info.sourceSpecimenCount = collectionEvent
            .getSourceSpecimensCount(true);
        info.aliquotedSpecimenCount = collectionEvent
            .getAliquotedSpecimensCount(true);
        info.comment = collectionEvent.getComment();
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
        Assert.isNotNull(row);
        return row.collectionEvent;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }

}
