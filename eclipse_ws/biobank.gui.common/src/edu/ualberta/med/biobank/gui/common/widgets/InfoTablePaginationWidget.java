package edu.ualberta.med.biobank.gui.common.widgets;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.gui.common.BgcPlugin;

public class InfoTablePaginationWidget extends Composite {

    public class PageInformation {
        public int page;
        public int rowsPerPage;
        public int pageTotal;
    }

    public static final int FIRST_PAGE_BUTTON = 1;

    public static final int PREV_PAGE_BUTTON = 2;

    public static final int NEXT_PAGE_BUTTON = 4;

    public static final int LAST_PAGE_BUTTON = 8;

    private IInfoTalePagination paginator;

    private Button firstButton;

    private Button prevButton;

    private Button nextButton;

    private Button lastButton;

    private Label pageLabel;

    protected PageInformation pageInfo;

    public InfoTablePaginationWidget(Composite parent, int style,
        IInfoTalePagination paginator, int buttonsEnabledOnInit, int rowsPerPage) {
        super(parent, style);
        this.paginator = paginator;

        setLayout(new GridLayout(5, false));

        firstButton = new Button(this, SWT.NONE);
        firstButton.setImage(BgcPlugin.getDefault().getImageRegistry()
            .get(BgcPlugin.IMG_RESULTSET_FIRST));
        firstButton
            .setToolTipText(Messages.AbstractInfoTableWidget_first_label);
        firstButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                firstPageInternal();
            }
        });

        prevButton = new Button(this, SWT.NONE);
        prevButton.setImage(BgcPlugin.getDefault().getImageRegistry()
            .get(BgcPlugin.IMG_RESULTSET_PREV));
        prevButton
            .setToolTipText(Messages.AbstractInfoTableWidget_previous_label);
        prevButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                prevPageInternal();
            }
        });

        pageLabel = new Label(this, SWT.NONE);

        nextButton = new Button(this, SWT.NONE);
        nextButton.setImage(BgcPlugin.getDefault().getImageRegistry()
            .get(BgcPlugin.IMG_RESULTSET_NEXT));
        nextButton.setToolTipText(Messages.AbstractInfoTableWidget_next_label);
        nextButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                nextPageInternal();
            }
        });

        lastButton = new Button(this, SWT.NONE);
        lastButton.setImage(BgcPlugin.getDefault().getImageRegistry()
            .get(BgcPlugin.IMG_RESULTSET_LAST));
        lastButton.setToolTipText(Messages.AbstractInfoTableWidget_last_label);
        lastButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                lastPageInternal();
            }
        });

        if ((buttonsEnabledOnInit & FIRST_PAGE_BUTTON) > 0) {
            firstButton.setEnabled(false);
        }

        if ((buttonsEnabledOnInit & PREV_PAGE_BUTTON) > 0) {
            prevButton.setEnabled(false);
        }

        if ((buttonsEnabledOnInit & NEXT_PAGE_BUTTON) > 0) {
            nextButton.setEnabled(false);
        }

        if ((buttonsEnabledOnInit & LAST_PAGE_BUTTON) > 0) {
            lastButton.setEnabled(false);
        }

        // do not display it yet, wait till collection is added
        setVisible(false);
        GridData gd = new GridData(SWT.END, SWT.TOP, true, false);
        gd.exclude = false;
        setLayoutData(gd);
        layout(true, true);

        pageInfo = new PageInformation();
        pageInfo.rowsPerPage = rowsPerPage;
        pageInfo.page = 0;

        setPageLabelText();
    }

    protected void enableWidgets(boolean enable) {
        if (enable && (pageInfo.page > 0)) {
            firstButton.setEnabled(true);
            prevButton.setEnabled(true);
        } else {
            firstButton.setEnabled(false);
            prevButton.setEnabled(false);
        }

        if (enable && (pageInfo.page < (pageInfo.pageTotal - 1))) {
            lastButton.setEnabled(true);
            nextButton.setEnabled(true);
        } else {
            lastButton.setEnabled(false);
            nextButton.setEnabled(false);
        }
    }

    private void firstPageInternal() {
        pageInfo.page = 0;
        firstButton.setEnabled(false);
        prevButton.setEnabled(false);
        lastButton.setEnabled(true);
        nextButton.setEnabled(true);
        paginator.firstPage();
        newPage();
    }

    private void prevPageInternal() {
        if (pageInfo.page == 0)
            return;
        pageInfo.page--;
        if (pageInfo.page == 0) {
            firstButton.setEnabled(false);
            prevButton.setEnabled(false);
        }
        if (pageInfo.page == (pageInfo.pageTotal - 2)) {
            lastButton.setEnabled(true);
            nextButton.setEnabled(true);
        }
        paginator.prevPage();
        newPage();
    }

    private void nextPageInternal() {
        if (pageInfo.page >= pageInfo.pageTotal)
            return;
        pageInfo.page++;
        if (pageInfo.page == 1) {
            firstButton.setEnabled(true);
            prevButton.setEnabled(true);
        }
        if (pageInfo.page == (pageInfo.pageTotal - 1)) {
            lastButton.setEnabled(false);
            nextButton.setEnabled(false);
        }
        paginator.nextPage();
        newPage();
    }

    private void lastPageInternal() {
        pageInfo.page = pageInfo.pageTotal - 1;
        firstButton.setEnabled(true);
        prevButton.setEnabled(true);
        lastButton.setEnabled(false);
        nextButton.setEnabled(false);
        paginator.lastPage();
        newPage();
    }

    protected void newPage() {
        setPageLabelText();
    }

    public void setPageLabelText() {
        if (pageInfo.pageTotal > 0) {
            pageLabel.setText(NLS.bind("Page: {0} of {1}", pageInfo.page + 1,
                +pageInfo.pageTotal));
        } else {
            pageLabel.setText(NLS.bind("Page: {0} of ?", pageInfo.page + 1));
        }
    }

    public int getCurrentPage() {
        return pageInfo.page;
    }

    public int getRowsPerPage() {
        return pageInfo.rowsPerPage;
    }

    public int getTotalPages() {
        return pageInfo.pageTotal;
    }

    public boolean setTableMaxRows(int tableMaxRows) {
        boolean result = false;

        if ((pageInfo.rowsPerPage != 0)
            && (tableMaxRows > pageInfo.rowsPerPage)) {
            Double size = new Double(tableMaxRows);
            Double pageSize = new Double(pageInfo.rowsPerPage);
            pageInfo.pageTotal = Double.valueOf(Math.ceil(size / pageSize))
                .intValue();
            result = true;
            if (pageInfo.page == pageInfo.pageTotal)
                pageInfo.page--;
        }
        return result;
    }

}
