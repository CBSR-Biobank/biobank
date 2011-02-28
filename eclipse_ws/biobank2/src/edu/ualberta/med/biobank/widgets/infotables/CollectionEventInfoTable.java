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

    class TableRowData {
        CollectionEventWrapper collectionEvent;
        Integer visitNumber;
        String dateDrawn;
        Integer sourceSpecimenCount;
        Integer aliquotedSpecimenCount;
        String comment;

        @Override
        public String toString() {
            return StringUtils.join(
                new String[] {
                    visitNumber.toString(),
                    dateDrawn,
                    (sourceSpecimenCount != null) ? sourceSpecimenCount
                        .toString() : "0",
                    comment,
                    (aliquotedSpecimenCount != null) ? aliquotedSpecimenCount
                        .toString() : "0" }, "\t");
        }
    }

    private static final String[] HEADINGS = new String[] {
        Messages.getString("CollectionEventInfoTable.header.visitNumber"),
        Messages.getString("CollectionEventInfoTable.header.dateDrawn"),
        Messages
            .getString("CollectionEventInfoTable.header.numSourceSpecimens"),
        Messages
            .getString("CollectionEventInfoTable.header.numAliquotedSpecimens"),
        Messages.getString("CollectionEventInfoTable.header.comment") };

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
                        return "loading...";
                    }
                    return "";
                }
                switch (columnIndex) {
                case 0:
                    return info.visitNumber.toString();
                case 1:
                    return info.dateDrawn;
                case 2:
                    return (info.sourceSpecimenCount != null) ? info.sourceSpecimenCount
                        .toString() : "0";
                case 3:
                    return (info.aliquotedSpecimenCount != null) ? info.aliquotedSpecimenCount
                        .toString() : "0";
                case 4:
                    return info.comment;

                default:
                    return "";
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
        info.dateDrawn = collectionEvent.getFormattedDateDrawn();
        info.sourceSpecimenCount = collectionEvent.getOriginalSpecimensCount();
        info.aliquotedSpecimenCount = collectionEvent
            .getAliquotedSpecimensCount();
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
