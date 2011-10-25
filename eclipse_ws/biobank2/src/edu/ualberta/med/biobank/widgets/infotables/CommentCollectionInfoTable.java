package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.common.wrappers.UserWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;

public class CommentCollectionInfoTable extends InfoTableWidget {

    private static class TableRowData {
        public String comment;
        public UserWrapper user;
        public Date date;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { comment.toString(),
                user.toString(), date.toString() });
        }
    }

    private static final String[] HEADINGS = new String[] { "Comment", "User",
        "Date" };

    public CommentCollectionInfoTable(Composite parent,
        List<CommentWrapper> collection) {
        super(parent, collection, HEADINGS, CommentWrapper.class);
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData item = (TableRowData) ((BiobankCollectionModel) element).o;
                switch (columnIndex) {
                case 0:
                    return item.comment;
                case 1:
                    return item.user.getLogin();
                case 2:
                    return DateFormatter.formatAsDate(item.date);
                default:
                    return ""; //$NON-NLS-1$
                }
            }
        };
    }

    @Override
    public Object getCollectionModelObject(Object o) throws Exception {
        TableRowData info = new TableRowData();
        CommentWrapper comment = (CommentWrapper) o;
        info.comment = comment.getText();
        info.user = comment.getUser();
        info.date = comment.getDateAdded();
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
        return new BiobankTableSorter() {
            private static final long serialVersionUID = 1L;

            @Override
            public int compare(Object o, Object o2) {
                CommentWrapper comment1 = (CommentWrapper) o;
                CommentWrapper comment2 = (CommentWrapper) o2;
                return comment1.getDateAdded().compareTo(
                    comment2.getDateAdded());
            }
        };
    }
}
