package edu.ualberta.med.biobank.views;

import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISourceProviderListener;
import org.eclipse.ui.part.ViewPart;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.LoginPermissionSessionState;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.utils.SearchType;

public class SearchView extends ViewPart {
    private static final I18n i18n = I18nFactory.getI18n(SearchView.class);

    @SuppressWarnings("nls")
    public static final String ID = "edu.ualberta.med.biobank.views.SearchView";

    private BgcBaseText searchText;
    private ComboViewer searchTypeCombo;

    private Button searchButton;

    protected boolean loggedIn;
    protected boolean allowed;

    private ISourceProviderListener sourceProviderListener;

    @SuppressWarnings("nls")
    @Override
    public void createPartControl(Composite parent) {
        parent.setLayout(new GridLayout(2, false));
        // listen to login state
        sourceProviderListener = new ISourceProviderListener() {
            @Override
            public void sourceChanged(int sourcePriority,
                @SuppressWarnings("rawtypes") Map sourceValuesByName) {
            }

            @Override
            public void sourceChanged(int sourcePriority, String sourceName,
                Object sourceValue) {
                if (sourceName
                    .equals(LoginPermissionSessionState.LOGIN_STATE_SOURCE_NAME)) {
                    loggedIn = sourceValue.equals(LoginPermissionSessionState.LOGGED_IN);
                    setEnabled();
                }
            }
        };
        BgcPlugin.getLoginStateSourceProvider().addSourceProviderListener(
            sourceProviderListener);

        allowed = true;
        // try {
        // TODO: not done
        // allowed = SessionManager.getAppService().isAllowed(new
        // SearchViewPermission(0, 0));
        // } catch (ApplicationException e1) {
        // BgcPlugin.openAccessDeniedErrorMessage(e1);
        // allowed = false;
        // }

        searchTypeCombo = new ComboViewer(parent);
        searchTypeCombo.setContentProvider(new ArrayContentProvider());
        searchTypeCombo.setInput(SearchType.values());
        searchTypeCombo.getCombo().select(0);
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;

        searchTypeCombo.getCombo().setLayoutData(gd);
        searchText = new BgcBaseText(parent, SWT.NONE);
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        searchText.setLayoutData(gd);
        searchText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.character == SWT.CR) {
                    search();
                }
            }
        });

        searchButton = new Button(parent, SWT.PUSH);
        searchButton.setText(
            // button label.
            i18n.trc("search (verb)", "Search"));
        searchButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                search();
            }
        });

        loggedIn = BgcPlugin.getLoginStateSourceProvider().getCurrentState()
            .get(LoginPermissionSessionState.LOGIN_STATE_SOURCE_NAME)
            .equals(LoginPermissionSessionState.LOGGED_IN);
        setEnabled();

    }

    @Override
    public void setFocus() {
        searchTypeCombo.getCombo().setFocus();
    }

    @Override
    public void dispose() {
        if (sourceProviderListener != null)
            BgcPlugin.getLoginStateSourceProvider()
                .removeSourceProviderListener(sourceProviderListener);
        super.dispose();
    }

    private void search() {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            @SuppressWarnings("nls")
            @Override
            public void run() {
                String searchString = searchText.getText().trim();

                SearchType type =
                    (SearchType) ((IStructuredSelection) searchTypeCombo
                        .getSelection()).getFirstElement();
                try {
                    List<ModelWrapper<?>> res = type.search(
                        searchString, SessionManager.getUser()
                            .getCurrentWorkingCenter());
                    if (res != null && res.size() > 0) {
                        type.processResults(res);
                    } else {
                        BgcPlugin.openInformation(
                            // dialog title.
                            i18n.tr("Search Result"),
                            // dialog message.
                            i18n.tr("no result"));
                    }
                } catch (Exception ex) {
                    BgcPlugin.openAsyncError(
                        // dialog title.
                        i18n.tr("Search error"), ex);
                }
            }
        });
    }

    public void setEnabled() {
        if (!searchText.isDisposed()) {
            searchText.setEnabled(loggedIn && allowed);
            searchTypeCombo.getCombo().setEnabled(loggedIn);
            searchButton.setEnabled(loggedIn);
        }
    }
}
