package edu.ualberta.med.biobank.gui.common.widgets;

import java.text.MessageFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.gui.common.BgcPlugin;

public class PaginationWidget extends BgcBaseWidget {
    private static final I18n i18n = I18nFactory
        .getI18n(PaginationWidget.class);

    @SuppressWarnings("nls")
    private static final String PREVIOUS_PAGE_BUTTON_TOOLTIP = i18n
        .tr("Previous Page");
    @SuppressWarnings("nls")
    private static final String FIRST_PAGE_BUTTON_TOOLTIP = i18n
        .tr("First Page");
    @SuppressWarnings("nls")
    private static final String NEXT_PAGE_BUTTON_TOOLTIP = i18n.tr("Next Page");
    @SuppressWarnings("nls")
    private static final String LAST_PAGE_BUTTON_TOOLTIP = i18n.tr("Last Page");
    @SuppressWarnings("nls")
    private static final String PAGE_X_OF_UNKNOWN = i18n.tr("Page: {0} of ?");
    @SuppressWarnings("nls")
    private static final String PAGE_X_OF_Y = i18n.tr("Page: {0} of {1}");

    class PageInformation {
        public int page;
        public int rowsPerPage;
        public int pageTotal;
    }

    public static final int FIRST_PAGE_BUTTON = 1;

    public static final int PREV_PAGE_BUTTON = 2;

    public static final int NEXT_PAGE_BUTTON = 4;

    public static final int LAST_PAGE_BUTTON = 8;

    public static final int TOTAL_PAGES_UNKNOWN = -1;

    private final IInfoTablePagination paginator;

    private final Button firstButton;

    private final Button prevButton;

    private final Button nextButton;

    private final Button lastButton;

    private final Label pageLabel;

    protected PageInformation pageInfo;

    public PaginationWidget(Composite parent, int style, IInfoTablePagination paginator,
        int buttonsEnabledOnInit, int rowsPerPage) {
        super(parent, style);
        this.paginator = paginator;

        setLayout(new GridLayout(5, false));

        firstButton = new Button(this, SWT.NONE);
        firstButton.setImage(BgcPlugin.getDefault().getImage(BgcPlugin.Image.RESULTSET_FIRST));
        firstButton
            .setToolTipText(FIRST_PAGE_BUTTON_TOOLTIP);
        firstButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                firstPageInternal();
            }
        });

        prevButton = new Button(this, SWT.NONE);
        prevButton.setImage(BgcPlugin.getDefault().getImage(BgcPlugin.Image.RESULTSET_PREV));
        prevButton
            .setToolTipText(PREVIOUS_PAGE_BUTTON_TOOLTIP);
        prevButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                prevPageInternal();
            }
        });

        pageLabel = new Label(this, SWT.NONE);

        nextButton = new Button(this, SWT.NONE);
        nextButton.setImage(BgcPlugin.getDefault().getImage(BgcPlugin.Image.RESULTSET_NEXT));
        nextButton.setToolTipText(NEXT_PAGE_BUTTON_TOOLTIP);
        nextButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                nextPageInternal();
            }
        });

        lastButton = new Button(this, SWT.NONE);
        lastButton.setImage(BgcPlugin.getDefault().getImage(BgcPlugin.Image.RESULTSET_LAST));
        lastButton.setToolTipText(LAST_PAGE_BUTTON_TOOLTIP);
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

        pageInfo = new PageInformation();
        pageInfo.rowsPerPage = rowsPerPage;
        pageInfo.page = 0;
        pageInfo.pageTotal = TOTAL_PAGES_UNKNOWN;

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

        if (enable
            && ((pageInfo.pageTotal == TOTAL_PAGES_UNKNOWN) || (pageInfo.page < (pageInfo.pageTotal - 1)))) {
            nextButton.setEnabled(true);
        } else {
            nextButton.setEnabled(false);
        }

        if (enable && (pageInfo.pageTotal != TOTAL_PAGES_UNKNOWN)
            && (pageInfo.page < (pageInfo.pageTotal - 1))) {
            lastButton.setEnabled(true);
        } else {
            lastButton.setEnabled(false);
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
        if (pageInfo.page == 0) {
            return;
        }

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
        if ((pageInfo.pageTotal >= 0) && (pageInfo.page >= pageInfo.pageTotal))
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
        if (pageInfo.pageTotal == TOTAL_PAGES_UNKNOWN) {
            pageLabel.setText(MessageFormat.format(PAGE_X_OF_UNKNOWN,
                pageInfo.page + 1));
        } else {
            pageLabel.setText(MessageFormat.format(PAGE_X_OF_Y,
                pageInfo.page + 1, pageInfo.pageTotal));
        }
        layout(true);
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

        if (tableMaxRows < 0) {
            pageInfo.pageTotal = TOTAL_PAGES_UNKNOWN;
        } else if ((pageInfo.rowsPerPage != 0)
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
