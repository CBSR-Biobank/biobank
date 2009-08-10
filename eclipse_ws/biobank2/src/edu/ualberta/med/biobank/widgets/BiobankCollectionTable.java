package edu.ualberta.med.biobank.widgets;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Widget used to display tabular data. Used mostly by view forms.
 */
public class BiobankCollectionTable extends BiobankWidget {

    private TableViewer tableViewer;

    public BiobankCollectionTable(Composite parent, int style,
        String[] headings, Object[] data) {
        this(parent, style, headings, null, data);
    }

    public BiobankCollectionTable(Composite parent, int style,
        String[] headings, int bounds[], Object[] data) {
        super(parent, style);

        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        setLayout(new GridLayout(1, false));

        tableViewer = new TableViewer(this, SWT.BORDER | SWT.MULTI
            | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.VIRTUAL);
        tableViewer.setLabelProvider(new BiobankLabelProvider());
        tableViewer.setContentProvider(new ArrayContentProvider());

        Table table = tableViewer.getTable();
        table.setLayout(new TableLayout());
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 100;
        table.setLayoutData(gd);
        // table.setFont(getFont());
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        int index = 0;
        for (String name : headings) {
            final TableColumn col = new TableColumn(table, SWT.NONE);
            col.setText(name);
            if (bounds == null || bounds[index] == -1) {
                col.pack();
            } else {
                col.setWidth(bounds[index]);
            }
            col.setResizable(true);
            col.addListener(SWT.SELECTED, new Listener() {
                public void handleEvent(Event event) {
                    col.pack();
                }
            });
            index++;
        }
        tableViewer.setColumnProperties(headings);
        tableViewer.setUseHashlookup(true);
        if (data != null) {
            tableViewer.setInput(data);
        }
    }

    public TableViewer getTableViewer() {
        return tableViewer;
    }

    public void adaptToToolkit(FormToolkit toolkit, boolean paintBorder) {
        adaptToToolkit(toolkit);
        if (paintBorder) {
            toolkit.paintBordersFor(this);
        }
    }

}
