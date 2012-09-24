package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.action.exception.BatchOpException;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.gui.common.widgets.BgcTableSorter;
import edu.ualberta.med.biobank.gui.common.widgets.DefaultAbstractInfoTableWidget;
import edu.ualberta.med.biobank.widgets.infotables.MembershipInfoTable.MultilineHandler;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class BatchOpExceptionTable
    extends DefaultAbstractInfoTableWidget<BatchOpException<?>> {
    public static final I18n i18n = I18nFactory
        .getI18n(MembershipInfoTable.class);
    public static final int ROWS_PER_PAGE = 7;
    @SuppressWarnings("nls")
    private static final String[] HEADINGS = new String[] {
        i18n.tr("Line"),
        i18n.tr("Error Message") };

    public BatchOpExceptionTable(Composite parent,
        Collection<BatchOpException<?>> errors) {
        super(parent, HEADINGS, ROWS_PER_PAGE);

        setCollection(errors);

        MultilineHandler handler = new MultilineHandler();

        Table table = getTableViewer().getTable();
        table.addListener(SWT.MeasureItem, handler);
        table.addListener(SWT.PaintItem, handler);
        table.addListener(SWT.EraseItem, handler);
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @SuppressWarnings("nls")
            @Override
            public String getColumnText(Object element, int columnIndex) {
                BatchOpException<?> e = (BatchOpException<?>) element;
                switch (columnIndex) {
                case 0:
                    return String.valueOf(e.getLineNumber());
                case 1:
                    return String.valueOf(e.getMessage());
                default:
                    return StringUtil.EMPTY_STRING;
                }
            }
        };
    }

    @Override
    protected BgcTableSorter getTableSorter() {
        return null;
    }

    @Override
    protected Boolean canEdit(BatchOpException<?> target)
        throws ApplicationException {
        return true;
    }

    @Override
    protected Boolean canDelete(BatchOpException<?> target)
        throws ApplicationException {
        return true;
    }

    @Override
    protected Boolean canView(BatchOpException<?> target)
        throws ApplicationException {
        return true;
    };
}
