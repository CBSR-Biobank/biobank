package edu.ualberta.med.biobank.views;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.ISourceProviderListener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.ISourceProviderService;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.sourceproviders.SessionState;
import edu.ualberta.med.biobank.treeview.RootNode;
import edu.ualberta.med.biobank.widgets.AdapterTreeWidget;
import edu.ualberta.med.biobank.widgets.BiobankText;

public abstract class AbstractAdministrationView extends
    AbstractViewWithAdapterTree {

    protected BiobankText treeText;

    private Composite searchComposite;

    @Override
    public void createPartControl(Composite parent) {
        parent.setLayout(new GridLayout(1, false));
        GridLayout gl = new GridLayout(1, false);
        gl.marginWidth = 0;
        gl.marginHeight = 0;
        gl.horizontalSpacing = 0;
        gl.verticalSpacing = 0;
        parent.setLayout(gl);

        searchComposite = new Composite(parent, SWT.NONE);
        gl = new GridLayout(1, false);
        gl.marginWidth = 0;
        gl.marginHeight = 0;
        gl.horizontalSpacing = 0;
        gl.verticalSpacing = 0;
        gl.marginBottom = 5;
        gl.marginTop = 2;
        searchComposite.setLayout(gl);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        searchComposite.setLayoutData(gd);

        createTreeTextOptions(searchComposite);

        treeText = new BiobankText(searchComposite, SWT.SINGLE);
        treeText.addListener(SWT.DefaultSelection, new Listener() {
            @Override
            public void handleEvent(Event e) {
                internalSearch();
            }
        });
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        treeText.setLayoutData(gd);
        treeText.setToolTipText(getTreeTextToolTip());
        searchComposite.setEnabled(false);
        addWorkingCenterListener();

        adaptersTree = new AdapterTreeWidget(parent, false);
        getSite().setSelectionProvider(adaptersTree.getTreeViewer());
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        adaptersTree.setLayoutData(gd);

        rootNode = new RootNode();
        rootNode.setTreeViewer(adaptersTree.getTreeViewer());
        adaptersTree.getTreeViewer().setInput(rootNode);
        adaptersTree.getTreeViewer().expandAll();
    }

    private void addWorkingCenterListener() {
        ISourceProviderService service = (ISourceProviderService) PlatformUI
            .getWorkbench().getActiveWorkbenchWindow()
            .getService(ISourceProviderService.class);
        SessionState sessionSourceProvider = (SessionState) service
            .getSourceProvider(SessionState.HAS_WORKING_CENTER_SOURCE_NAME);
        sessionSourceProvider
            .addSourceProviderListener(new ISourceProviderListener() {
                @Override
                public void sourceChanged(int sourcePriority,
                    String sourceName, Object sourceValue) {
                    if (sourceValue != null) {
                        if (!searchComposite.isDisposed()) {
                            if (sourceValue instanceof Boolean)
                                searchComposite
                                    .setEnabled((Boolean) sourceValue);
                            if (sourceValue instanceof String)
                                searchComposite.setEnabled(new Boolean(
                                    (String) sourceValue));
                        }
                    }
                }

                @Override
                public void sourceChanged(int sourcePriority,
                    @SuppressWarnings("rawtypes") Map sourceValuesByName) {
                }
            });
    }

    protected abstract String getTreeTextToolTip();

    protected void createTreeTextOptions(
        @SuppressWarnings("unused") Composite parent) {
        // default do nothing
    }

    protected abstract void internalSearch();

    @Override
    public void reload() {
        if (!getTreeViewer().getControl().isDisposed()) {
            getTreeViewer().refresh(true);
            getTreeViewer().expandToLevel(3);
            setSearchFieldsEnablement(true);
        }
    }

    @Override
    public void clear() {
        super.clear();
        setSearchFieldsEnablement(false);
    }

    protected void setSearchFieldsEnablement(boolean enabled) {
        if (!searchComposite.isDisposed()) {
            searchComposite.setEnabled(enabled);
            for (Control c : searchComposite.getChildren()) {
                c.setEnabled(enabled);
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void setFocus() {
        treeText.setFocus();
    }

    @Override
    public void opened() {
        if (SessionManager.getInstance().isConnected())
            reload();
    }

}
