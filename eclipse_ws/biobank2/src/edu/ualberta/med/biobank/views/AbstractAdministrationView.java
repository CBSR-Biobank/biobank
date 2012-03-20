package edu.ualberta.med.biobank.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.treeview.NewRootNode;
import edu.ualberta.med.biobank.treeview.RootNode;
import edu.ualberta.med.biobank.widgets.trees.AdapterTreeWidget;

public abstract class AbstractAdministrationView extends
    AbstractViewWithAdapterTree {

    protected BgcBaseText treeText;

    private Composite searchComposite;

    @Override
    public void createPartControlInternal(Composite parent) {
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
        searchComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING,
            true, false));

        createTreeTextOptions(searchComposite);

        treeText = new BgcBaseText(searchComposite, SWT.SINGLE);
        treeText.addListener(SWT.DefaultSelection, new Listener() {
            @Override
            public void handleEvent(Event e) {
                internalSearch();
            }
        });
        treeText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true,
            false));
        treeText.setToolTipText(getTreeTextToolTip());
        searchComposite.setEnabled(false);

        adaptersTree = new AdapterTreeWidget(parent, false);
        getSite().setSelectionProvider(adaptersTree.getTreeViewer());

        createRootNode();
        adaptersTree.getTreeViewer().setInput(rootNode);
        adaptersTree.getTreeViewer().expandAll();
    }

    // FIXME temporary method until only one possible rootnode is possible
    protected abstract void createRootNode();

    protected void createOldRootNode() {
        rootNode = new RootNode();
        ((RootNode) rootNode).setTreeViewer(adaptersTree.getTreeViewer());
    }

    protected void createNewRootNode() {
        rootNode = new NewRootNode();
        ((NewRootNode) rootNode).setTreeViewer(adaptersTree.getTreeViewer());
    }

    protected abstract String getTreeTextToolTip();

    protected void createTreeTextOptions(Composite parent) {
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
