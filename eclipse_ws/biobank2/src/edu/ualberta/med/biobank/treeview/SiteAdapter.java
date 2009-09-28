package edu.ualberta.med.biobank.treeview;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.forms.SiteEntryForm;
import edu.ualberta.med.biobank.forms.SiteViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.example.DeleteExampleQuery;

public class SiteAdapter extends AdapterBase {

    public static final int CLINICS_NODE_ID = 0;
    public static final int STUDIES_NODE_ID = 1;
    public static final int STORAGE_TYPES_NODE_ID = 2;
    public static final int STORAGE_CONTAINERS_NODE_ID = 3;

    /**
     * if true, enable normal actions of this adapter
     */
    private boolean enableActions = true;

    public SiteAdapter(AdapterBase parent, Site site) {
        this(parent, site, true);
    }

    public SiteAdapter(AdapterBase parent, Site site, boolean enableActions) {
        super(parent, site);
        this.enableActions = enableActions;
        if (enableActions) {
            addChild(new ClinicGroup(this, CLINICS_NODE_ID));
            addChild(new StudyGroup(this, STUDIES_NODE_ID));
            addChild(new ContainerTypeGroup(this, STORAGE_TYPES_NODE_ID));
            addChild(new ContainerGroup(this, STORAGE_CONTAINERS_NODE_ID));
        }
    }

    public void setSite(Site site) {
        object = site;
    }

    public Site getSite() {
        return (Site) object;
    }

    public AdapterBase getStudiesGroupNode() {
        return children.get(STUDIES_NODE_ID);
    }

    public AdapterBase getClinicGroupNode() {
        return children.get(CLINICS_NODE_ID);
    }

    public AdapterBase getContainerTypesGroupNode() {
        return children.get(STORAGE_TYPES_NODE_ID);
    }

    public AdapterBase getContainersGroupNode() {
        return children.get(STORAGE_CONTAINERS_NODE_ID);
    }

    @Override
    public String getName() {
        Site site = getSite();
        Assert.isNotNull(site, "site is null");
        return site.getName();
    }

    @Override
    public String getTitle() {
        return getTitle("Site");
    }

    @Override
    public void performDoubleClick() {
        if (enableActions) {
            openForm(new FormInput(this), SiteViewForm.ID);
        }
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        if (enableActions) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText("Edit Site");
            mi.addSelectionListener(new SelectionListener() {
                public void widgetSelected(SelectionEvent event) {
                    openForm(new FormInput(SiteAdapter.this), SiteEntryForm.ID);
                }

                public void widgetDefaultSelected(SelectionEvent e) {
                }
            });

            mi = new MenuItem(menu, SWT.PUSH);
            mi.setText("View Site");
            mi.addSelectionListener(new SelectionListener() {
                public void widgetSelected(SelectionEvent event) {
                    openForm(new FormInput(SiteAdapter.this), SiteViewForm.ID);
                }

                public void widgetDefaultSelected(SelectionEvent e) {
                }
            });
            mi = new MenuItem(menu, SWT.PUSH);
            mi.setText("Delete Site");
            mi.addSelectionListener(new SelectionListener() {
                public void widgetSelected(SelectionEvent event) {
                    Boolean confirm = MessageDialog.openConfirm(PlatformUI
                        .getWorkbench().getActiveWorkbenchWindow().getShell(),
                        "Confirm Delete",
                        "Are you sure you want to delete this site?");

                    if (confirm) {
                        delete();
                    }

                }

                public void widgetDefaultSelected(SelectionEvent e) {
                }
            });
        }
    }

    @Override
    public void delete() {
        // FIXME when wrapper is used : remove this method to use the
        // parent one
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            Site site = getSite();
            SDKQuery query = new DeleteExampleQuery(site);

            public void run() {
                if (site.getClinicCollection().size() > 0
                    || site.getContainerCollection().size() > 0
                    || site.getContainerTypeCollection().size() > 0
                    || site.getStudyCollection().size() > 0) {
                    BioBankPlugin
                        .openError(
                            "Error",
                            "Unable to delete site "
                                + site.getName()
                                + ". All defined children (studies, clinics, container types, and containers) must be removed first.");
                } else
                    try {
                        getAppService().executeQuery(query);
                        SiteAdapter.this.getParent().removeChild(
                            SiteAdapter.this);
                    } catch (ApplicationException e) {
                        BioBankPlugin.openAsyncError("Delete error", e);
                    }

            }
        });
    }

    @Override
    public void loadChildren(boolean updateNode) {

    }

    @Override
    public AdapterBase accept(NodeSearchVisitor visitor) {
        return visitor.visit(this);
    }

}