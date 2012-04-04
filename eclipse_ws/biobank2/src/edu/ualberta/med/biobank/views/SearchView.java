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

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.LoginSessionState;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.utils.SearchType;

public class SearchView extends ViewPart {

    public static final String ID = "edu.ualberta.med.biobank.views.SearchView"; 

    private BgcBaseText searchText;
    private ComboViewer searchTypeCombo;

    private Button searchButton;

    protected boolean loggedIn;
    protected boolean allowed;

    private ISourceProviderListener sourceProviderListener;

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
                    .equals(LoginSessionState.LOGIN_STATE_SOURCE_NAME)) {
                    loggedIn = sourceValue.equals(LoginSessionState.LOGGED_IN);
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
        searchButton.setText("Search");
        searchButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                search();
            }
        });

        loggedIn = BgcPlugin.getLoginStateSourceProvider().getCurrentState()
            .get(LoginSessionState.LOGIN_STATE_SOURCE_NAME)
            .equals(LoginSessionState.LOGGED_IN);
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
                            "Search Result",
                            "no result");
                    }
                } catch (Exception ex) {
                    BgcPlugin.openAsyncError(
                        "Search error", ex);
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
