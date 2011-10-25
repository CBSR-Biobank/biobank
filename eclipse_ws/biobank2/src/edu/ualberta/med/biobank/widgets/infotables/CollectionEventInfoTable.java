package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.formatters.NumberFormatter;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;

public class CollectionEventInfoTable extends InfoTableWidget {

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
        Messages.CollectionEventInfoTable_header_visitNumber,
        Messages.CollectionEventInfoTable_header_numSourceSpecimens,
        Messages.CollectionEventInfoTable_header_numAliquotedSpecimens,
        Messages.CollectionEventInfoTable_header_comment };

    public CollectionEventInfoTable(Composite parent,
        List<CollectionEventWrapper> collection) {
        super(parent, collection, HEADINGS, 10, CollectionEventWrapper.class);
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData info = (TableRowData) ((BiobankCollectionModel) element).o;
                if (info == null) {
                    if (columnIndex == 0) {
                        return Messages.infotable_loading_msg;
                    }
                    return ""; //$NON-NLS-1$
                }
                switch (columnIndex) {
                case 0:
                    return info.visitNumber.toString();
                case 1:
                    return NumberFormatter.format(info.sourceSpecimenCount);
                case 2:
                    return NumberFormatter.format(info.aliquotedSpecimenCount);
                case 3:
                    return info.comment;

                default:
                    return ""; //$NON-NLS-1$
                }
            }
        };
    }

    @Override
    public Object getCollectionModelObject(Object o) throws Exception {
        TableRowData info = new TableRowData();
        info.collectionEvent = (CollectionEventWrapper) o;
        info.visitNumber = info.collectionEvent.getVisitNumber();
        info.sourceSpecimenCount = info.collectionEvent
            .getSourceSpecimensCount(true);
        info.aliquotedSpecimenCount = info.collectionEvent
            .getAliquotedSpecimensCount(true);
        info.comment = info.collectionEvent.getCommentCollection(false)
            .toString();
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
