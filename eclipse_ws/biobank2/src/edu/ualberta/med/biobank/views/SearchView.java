package edu.ualberta.med.biobank.views;

import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISourceProvider;
import org.eclipse.ui.ISourceProviderListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.ISourceProviderService;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.sourceproviders.SiteSelectionState;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterFactory;
import edu.ualberta.med.biobank.utils.SearchType;

public class SearchView extends ViewPart {

    public static final String ID = "edu.ualberta.med.biobank.views.SearchView";

    private Text searchText;
    private ComboViewer searchTypeCombo;

    private ISourceProviderListener siteStateListener;

    private Button searchButton;

    @Override
    public void createPartControl(Composite parent) {
        parent.setLayout(new GridLayout(2, false));

        searchTypeCombo = new ComboViewer(parent);
        searchTypeCombo.setContentProvider(new ArrayContentProvider());
        searchTypeCombo.setInput(SearchType.values());
        searchTypeCombo.getCombo().select(0);
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        searchTypeCombo.getCombo().setLayoutData(gd);

        searchText = new Text(parent, SWT.BORDER);
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        searchText.setLayoutData(gd);

        searchButton = new Button(parent, SWT.PUSH);
        searchButton.setText("Search");
        searchButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                search();
            }
        });

        ISourceProvider siteSelectionStateSourceProvider = getSiteSelectionStateSourceProvider();
        Integer siteId = (Integer) siteSelectionStateSourceProvider
            .getCurrentState().get(SiteSelectionState.SITE_SELECTION_ID);
        setSearchEnable(siteId);
        siteStateListener = new ISourceProviderListener() {
            @Override
            public void sourceChanged(int sourcePriority, String sourceName,
                Object sourceValue) {
                if (sourceName.equals(SiteSelectionState.SITE_SELECTION_ID)) {
                    setSearchEnable((Integer) sourceValue);
                }
            }

            @SuppressWarnings("unchecked")
            @Override
            public void sourceChanged(int sourcePriority, Map sourceValuesByName) {
            }
        };

        siteSelectionStateSourceProvider
            .addSourceProviderListener(siteStateListener);
    }

    private void setSearchEnable(Integer siteId) {
        boolean enable = (siteId != null && siteId >= 0);
        searchButton.setEnabled(enable);
    }

    @Override
    public void setFocus() {
        searchTypeCombo.getCombo().setFocus();
    }

    @Override
    public void dispose() {
        super.dispose();
        getSiteSelectionStateSourceProvider().removeSourceProviderListener(
            siteStateListener);
    }

    private ISourceProvider getSiteSelectionStateSourceProvider() {
        IWorkbenchWindow window = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow();
        ISourceProviderService service = (ISourceProviderService) window
            .getService(ISourceProviderService.class);
        ISourceProvider siteSelectionStateSourceProvider = service
            .getSourceProvider(SiteSelectionState.SITE_SELECTION_ID);
        return siteSelectionStateSourceProvider;
    }

    private void search() {
        String searchString = searchText.getText();
        SearchType type = (SearchType) ((IStructuredSelection) searchTypeCombo
            .getSelection()).getFirstElement();
        try {
            List<? extends ModelWrapper<?>> res = type.search(searchString);
            if (res != null && res.size() > 0) {
                int size = res.size();
                if (size == 1) {
                    openResult(res.get(0));
                } else {
                    boolean open = MessageDialog.openQuestion(PlatformUI
                        .getWorkbench().getActiveWorkbenchWindow().getShell(),
                        "Search Result", "Found " + size
                            + " results. Do you want to open all of them ?");
                    if (open) {
                        for (ModelWrapper<?> wrapper : res) {
                            openResult(wrapper);
                        }
                    }
                }
            } else {
                BioBankPlugin.openInformation("Search Result", "no result");
            }
        } catch (Exception ex) {
            BioBankPlugin.openAsyncError("Search error", ex);
        }
    }

    public void openResult(ModelWrapper<?> wrapper) {
        AdapterBase adapter = AdapterFactory.getAdapter(wrapper);
        if (adapter != null) {
            adapter.performDoubleClick();
        }
    }

}
