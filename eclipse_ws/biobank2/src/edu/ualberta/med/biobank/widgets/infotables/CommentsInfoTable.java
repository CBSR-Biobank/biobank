package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.common.wrappers.UserWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * TODO: it would be nice to set the cells that contain the comment message to
 * automatically wrap the text.
 * 
 */
public class CommentsInfoTable extends InfoTableWidget<CommentWrapper> {

    final int TEXT_MARGIN = 3;

    protected static class TableRowData {
        public CommentWrapper comment;
        public UserWrapper user;
        public Date date;
        public String message;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { user.toString(),
                date.toString(), message.toString()
            });
        }
    }

    private static final String[] HEADINGS = new String[] {
        "User",
        "Date",
        "Message" };

    public CommentsInfoTable(Composite parent,
        List<CommentWrapper> collection) {
        super(parent, collection, HEADINGS, CommentWrapper.class);

        Table table = getTableViewer().getTable();

        /*
         * NOTE: MeasureItem, PaintItem and EraseItem are called repeatedly.
         * Therefore, it is critical for performance that these methods be as
         * efficient as possible.
         */
        table.addListener(SWT.MeasureItem, new Listener() {
            @Override
            public void handleEvent(Event event) {
                TableItem item = (TableItem) event.item;
                String text = item.getText(event.index);
                Point size = event.gc.textExtent(text);
                event.width = size.x + 2 * TEXT_MARGIN;
                event.height = Math.max(event.height, size.y + TEXT_MARGIN);
            }
        });
        table.addListener(SWT.EraseItem, new Listener() {
            @Override
            public void handleEvent(Event event) {
                event.detail &= ~SWT.FOREGROUND;
            }
        });
        table.addListener(SWT.PaintItem, new Listener() {
            @Override
            public void handleEvent(Event event) {
                TableItem item = (TableItem) event.item;
                String text = item.getText(event.index);
                /* center column 1 vertically */
                int yOffset = 0;
                if (event.index == 1) {
                    Point size = event.gc.textExtent(text);
                    yOffset = Math.max(0, (event.height - size.y) / 2);
                }
                event.gc.drawText(text, event.x + TEXT_MARGIN, event.y
                    + yOffset, true);
            }
        });
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
                        return "loading...";
                    }
                    return ""; 
                }
                switch (columnIndex) {
                case 0:
                    return StringUtils.join(wrapText(item.user.getLogin(), 10),
                        "\n");
                case 1:
                    return DateFormatter.formatAsDateTime(item.date);
                case 2:
                    return StringUtils.join(wrapText(item.message, 80), "\n");
                default:
                    return ""; 
                }
            }
        };
    }

    @Override
    public Object getCollectionModelObject(Object o) throws Exception {
        TableRowData info = new TableRowData();
        CommentWrapper comm = (CommentWrapper) o;
        info.comment = comm;
        info.message = comm.getMessage();
        info.user = comm.getUser();
        info.date = comm.getCreatedAt();
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null) return null;
        return ((TableRowData) o).toString();
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return new BiobankTableSorter() {
            private static final long serialVersionUID = 1L;

            @Override
            public int compare(Object o, Object o2) {
                if (o == null || o2 == null)
                    return 0;
                CommentWrapper comment1 = (CommentWrapper) o;
                CommentWrapper comment2 = (CommentWrapper) o2;
                return comment1.getCreatedAt().compareTo(
                    comment2.getCreatedAt());
            }
        };
    }

    @Override
    public CommentWrapper getSelection() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Boolean canEdit(CommentWrapper target)
        throws ApplicationException {
        return false;
    }

    @Override
    protected Boolean canDelete(CommentWrapper target)
        throws ApplicationException {
        return false;
    }

    @Override
    protected Boolean canView(CommentWrapper target)
        throws ApplicationException {
        return false;
    }
}
