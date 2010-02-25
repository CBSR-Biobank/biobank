package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import edu.ualberta.med.biobank.widgets.BiobankWidget;
import edu.ualberta.med.biobank.widgets.ReportsLabelProvider;

public class PagedTableWidget<T> extends BiobankWidget {

    private TableViewer tableViewer;

    private Integer pages;
    private Integer page;
    private Integer pageSize;
    protected Label previousLabel;
    protected Label nextLabel;
    protected Label pageLabel;

    private List<Object> input;

    // private static Logger LOGGER = Logger.getLogger(InfoTableWidget.class
    // .getName());

    public PagedTableWidget(Composite parent, List<Object> input,
        String[] headings, int[] columnWidths) {
        super(parent, SWT.NONE);

        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        setLayout(new GridLayout(1, false));

        tableViewer = new TableViewer(this, SWT.BORDER | SWT.FULL_SELECTION
            | SWT.VIRTUAL);
        tableViewer.setLabelProvider(new ReportsLabelProvider());
        tableViewer.setContentProvider(new ArrayContentProvider());

        final Table table = tableViewer.getTable();
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
            if (columnWidths == null || columnWidths[index] == -1) {
                col.pack();
            } else {
                col.setWidth(columnWidths[index]);
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

        page = 0;
        pageSize = 42;

        if (pageSize != null && pageSize != 0)
            pages = input.size() / pageSize + 1;
        else
            pages = 1;
        this.input = new ArrayList<Object>(input);

        refresh();
        addPageLabels(this);

    }

    public void addPageLabels(Composite parent) {
        Composite pageLabels = new Composite(parent, SWT.BORDER);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        pageLabels.setLayoutData(gd);
        pageLabels.setLayout(new GridLayout(3, true));

        previousLabel = new Label(pageLabels, SWT.NONE);
        previousLabel.setText("Previous");
        GridData prevLabelData = new GridData();
        prevLabelData.grabExcessHorizontalSpace = true;
        prevLabelData.horizontalAlignment = SWT.LEFT;
        prevLabelData.minimumWidth = 50;
        previousLabel.setLayoutData(prevLabelData);
        previousLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                if (page > 0) {
                    page--;
                    PagedTableWidget.this.refresh();
                    if (page == 0)
                        previousLabel.setEnabled(false);
                    if (page < pages - 1)
                        nextLabel.setEnabled(true);
                }
                Font font = previousLabel.getFont();
                FontData data = font.getFontData()[0];
                data.setStyle(SWT.NONE);
                previousLabel.setFont(new Font(getDisplay(), data));
                setPageLabelText();
            }

            @Override
            public void mouseDown(MouseEvent e) {
                Font font = previousLabel.getFont();
                FontData data = font.getFontData()[0];
                data.setStyle(SWT.BOLD);
                previousLabel.setFont(new Font(getDisplay(), data));
            }

        });

        pageLabel = new Label(pageLabels, SWT.NONE);
        setPageLabelText();
        GridData pageLabelData = new GridData();
        pageLabelData.grabExcessHorizontalSpace = true;
        pageLabelData.horizontalAlignment = SWT.CENTER;
        pageLabelData.minimumWidth = 150;
        pageLabel.setLayoutData(pageLabelData);

        nextLabel = new Label(pageLabels, SWT.NONE);
        nextLabel.setText("Next");
        GridData nextLabelData = new GridData();
        nextLabelData.grabExcessHorizontalSpace = true;
        nextLabelData.horizontalAlignment = SWT.RIGHT;
        nextLabelData.minimumWidth = 50;
        nextLabel.setLayoutData(nextLabelData);
        nextLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                if (page < pages - 1) {
                    page++;
                    PagedTableWidget.this.refresh();
                    if (page > 0)
                        previousLabel.setEnabled(true);
                    if (page == pages - 1)
                        nextLabel.setEnabled(false);
                }
                Font font = nextLabel.getFont();
                FontData data = font.getFontData()[0];
                data.setStyle(SWT.NONE);
                nextLabel.setFont(new Font(getDisplay(), data));
                setPageLabelText();
            }

            @Override
            public void mouseDown(MouseEvent e) {
                Font font = nextLabel.getFont();
                FontData data = font.getFontData()[0];
                data.setStyle(SWT.BOLD);
                nextLabel.setFont(new Font(getDisplay(), data));
            }
        });

        if (pages == 1) {
            previousLabel.setEnabled(false);
            nextLabel.setEnabled(false);
        } else
            previousLabel.setEnabled(false);

    }

    private void setPageLabelText() {
        pageLabel.setText("Page: " + (page + 1) + " of " + pages);
    }

    public void refresh() {
        int start = page * pageSize;
        int end = start + pageSize;
        end = Math.min(end, input.size());
        tableViewer.setInput(input.subList(start, end));
    }

    public void addDoubleClickListener(IDoubleClickListener listener) {
        tableViewer.addDoubleClickListener(listener);
    }

    public void addSelectionListener(SelectionListener listener) {
        tableViewer.getTable().addSelectionListener(listener);
    }

    public TableViewer getTableViewer() {
        return tableViewer;
    }

    @Override
    public void setEnabled(boolean enabled) {
        tableViewer.getTable().setEnabled(enabled);
    }

    public void reset() {
        input = new ArrayList<Object>();
        tableViewer.setInput(input);
        page = 0;
        pages = 1;
        setPageLabelText();
        previousLabel.setEnabled(false);
        nextLabel.setEnabled(false);
    }
}
