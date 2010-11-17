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
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.ISourceProviderService;

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

        adaptersTree = new AdapterTreeWidget(parent, false);
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        adaptersTree.setLayoutData(gd);

        rootNode = new RootNode();
        rootNode.setTreeViewer(adaptersTree.getTreeViewer());
        adaptersTree.getTreeViewer().setInput(rootNode);
        getSite().setSelectionProvider(adaptersTree.getTreeViewer());
        adaptersTree.getTreeViewer().expandAll();

        // listen to login state
        IWorkbenchWindow window = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow();
        ISourceProviderService service = (ISourceProviderService) window
            .getService(ISourceProviderService.class);
        SessionState sessionSourceProvider = (SessionState) service
            .getSourceProvider(SessionState.LOGIN_STATE_SOURCE_NAME);
        sessionSourceProvider
            .addSourceProviderListener(new ISourceProviderListener() {

                @SuppressWarnings("rawtypes")
                @Override
                public void sourceChanged(int sourcePriority,
                    Map sourceValuesByName) {
                }

                @Override
                public void sourceChanged(int sourcePriority,
                    String sourceName, Object sourceValue) {
                    if (sourceName.equals(SessionState.LOGIN_STATE_SOURCE_NAME)) {
                        setSearchFieldsEnablement(sourceValue
                            .equals(SessionState.LOGGED_IN));
                    }
                }
            });

    }

    protected void createTreeTextOptions(
        @SuppressWarnings("unused") Composite parent) {
        // default do nothing
    }

    protected abstract void internalSearch();

    @Override
    public void reload() {
        getTreeViewer().refresh(true);
        getTreeViewer().expandToLevel(3);
    }

    private void setSearchFieldsEnablement(boolean enabled) {
        searchComposite.setEnabled(enabled);
        for (Control c : searchComposite.getChildren()) {
            c.setEnabled(enabled);
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
        reload();
    }

}
