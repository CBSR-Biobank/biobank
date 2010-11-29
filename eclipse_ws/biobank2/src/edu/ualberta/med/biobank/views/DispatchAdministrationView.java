package edu.ualberta.med.biobank.views;

import java.util.Date;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.dispatch.DispatchSearchedNode;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;

public class DispatchAdministrationView extends AbstractAdministrationView {

    public static final String ID = "edu.ualberta.med.biobank.views.DispatchAdministrationView";

    private Button radioWaybill;

    private Button radioDateSent;

    private Composite dateComposite;

    private DateTimeWidget dateWidget;

    private DispatchSearchedNode searchedNode;

    List<SiteWrapper> siteNodes;

    private Button radioDateReceived;

    private static DispatchAdministrationView currentInstance;

    public DispatchAdministrationView() {
        currentInstance = this;
        SessionManager.addView(ID, this);
    }

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
    }

    public void createNodes() {
        try {
            siteNodes = SiteWrapper.getSites(SessionManager.getAppService());
        } catch (Exception e) {
            BioBankPlugin.openAsyncError("Failed to load sites", e);
        }

        for (SiteWrapper site : siteNodes) {
            DispatchSiteAdapter siteAdapter = new DispatchSiteAdapter(rootNode,
                site);
            siteAdapter.setParent(rootNode);
            rootNode.addChild(siteAdapter);
        }

        searchedNode = new DispatchSearchedNode(rootNode, 2);
        searchedNode.setParent(rootNode);
        rootNode.addChild(searchedNode);
    }

    @Override
    protected void createTreeTextOptions(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        layout.horizontalSpacing = 0;
        layout.marginHeight = 0;
        layout.verticalSpacing = 0;
        composite.setLayout(layout);

        radioWaybill = new Button(composite, SWT.RADIO);
        radioWaybill.setText("Waybill");
        radioWaybill.setSelection(true);
        radioWaybill.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radioWaybill.getSelection()) {
                    showTextOnly(true);
                }
            }
        });
        radioDateSent = new Button(composite, SWT.RADIO);
        radioDateSent.setText("Departed");
        radioDateSent.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radioDateSent.getSelection()) {
                    showTextOnly(false);
                }
            }
        });

        radioDateReceived = new Button(composite, SWT.RADIO);
        radioDateReceived.setText("Date Received");
        radioDateReceived.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radioDateReceived.getSelection()) {
                    showTextOnly(false);
                }
            }
        });

        dateComposite = new Composite(parent, SWT.NONE);
        layout = new GridLayout(2, false);
        layout.horizontalSpacing = 0;
        layout.marginHeight = 0;
        layout.verticalSpacing = 0;
        dateComposite.setLayout(layout);
        GridData gd = new GridData();
        gd.exclude = true;
        dateComposite.setLayoutData(gd);

        dateWidget = new DateTimeWidget(dateComposite, SWT.DATE, new Date());
        dateWidget.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                internalSearch();
            }
        });
        Button searchButton = new Button(dateComposite, SWT.PUSH);
        searchButton.setText("Go");
        searchButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                internalSearch();
            }
        });
    }

    protected void showTextOnly(boolean show) {
        treeText.setVisible(show);
        ((GridData) treeText.getLayoutData()).exclude = !show;
        dateComposite.setVisible(!show);
        ((GridData) dateComposite.getLayoutData()).exclude = show;
        treeText.getParent().layout(true, true);
    }

    @Override
    public void reload() {
        try {
            // SessionManager.getCurrentSite().reload();
        } catch (Exception e) {
            BioBankPlugin.openAsyncError("Unable to reload site information.",
                e);
        }
        rootNode.removeAll();
        createNodes();
        for (AdapterBase adaper : rootNode.getChildren()) {
            if (!adaper.equals(searchedNode))
                adaper.rebuild();
        }
        super.reload();
    }

    @Override
    protected void internalSearch() {
        try {
            List<? extends ModelWrapper<?>> searchedObject = search();
            if (searchedObject == null || searchedObject.size() == 0) {
                String msg = "No Dispatch found";
                if (radioWaybill.getSelection()) {
                    msg += " for waybill " + treeText.getText();
                } else {
                    msg += " for date "
                        + DateFormatter.formatAsDate(dateWidget.getDate());
                }
                BioBankPlugin.openMessage("Dispatch not found", msg);
            } else {
                showSearchedObjectsInTree(searchedObject, true);
                getTreeViewer().expandToLevel(searchedNode, 3);
            }
        } catch (Exception e) {
            BioBankPlugin.openError("Search error", e);
        }
    }

    protected List<DispatchWrapper> search() throws Exception {
        for (SiteWrapper site : siteNodes) {
            if (radioWaybill.getSelection()) {
                return DispatchWrapper.getDispatchesInSite(
                    SessionManager.getAppService(), treeText.getText().trim(),
                    site);
            } else {
                Date date = dateWidget.getDate();
                if (date != null) {
                    if (radioDateSent.getSelection())
                        return DispatchWrapper.getDispatchesInSiteByDateSent(
                            SessionManager.getAppService(), date, site);
                    else
                        return DispatchWrapper
                            .getDispatchesInSiteByDateReceived(
                                SessionManager.getAppService(), date, site);
                }
            }
        }
        return null;
    }

    protected void showSearchedObjectsInTree(
        List<? extends ModelWrapper<?>> searchedObjects, boolean doubleClick) {
        for (ModelWrapper<?> searchedObject : searchedObjects) {
            AdapterBase node = rootNode.search(searchedObject);
            if (node == null) {
                searchedNode.addSearchObject(searchedObject);
                searchedNode.performExpand();
                node = searchedNode.search(searchedObject);
            }
            if (node != null) {
                setSelectedNode(node);
                if (doubleClick) {
                    node.performDoubleClick();
                }
            }
        }
    }

    public static DispatchAdministrationView getCurrent() {
        return currentInstance;
    }

    public void clear() {
        rootNode.removeAll();
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    protected String getTreeTextToolTip() {
        return "Enter a dispatch waybill and hit enter";
    }

}
