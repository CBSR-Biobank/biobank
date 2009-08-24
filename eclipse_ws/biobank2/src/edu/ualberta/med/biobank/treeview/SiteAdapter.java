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
import org.springframework.remoting.RemoteAccessException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.SiteEntryForm;
import edu.ualberta.med.biobank.forms.SiteViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.example.DeleteExampleQuery;

public class SiteAdapter extends AdapterBase {
    public static final int STUDIES_NODE_ID = 0;
    public static final int CLINICS_NODE_ID = 1;
    public static final int STORAGE_TYPES_NODE_ID = 2;
    public static final int STORAGE_CONTAINERS_NODE_ID = 3;

    private Site site;

    /**
     * if true, enable normal actions of this adapter
     */
    private boolean enableActions = true;

    public SiteAdapter(AdapterBase parent, Site site) {
        this(parent, site, true);
    }

    public SiteAdapter(AdapterBase parent, Site site, boolean enableActions) {
        super(parent);
        this.site = site;
        this.enableActions = enableActions;
        if (enableActions) {
            addChild(new StudyGroup(this, STUDIES_NODE_ID));
            addChild(new ClinicGroup(this, CLINICS_NODE_ID));
            addChild(new ContainerTypeGroup(this, STORAGE_TYPES_NODE_ID));
            addChild(new ContainerGroup(this, STORAGE_CONTAINERS_NODE_ID));
        }
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public Site getSite() {
        return site;
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

    @Override
    public Integer getId() {
        Assert.isNotNull(site, "site is null");
        return site.getId();
    }

    @Override
    public String getName() {
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
                    deleteSite();
                }

                public void widgetDefaultSelected(SelectionEvent e) {
                }
            });
        }
    }

    @Override
    public void loadChildren(boolean updateNode) {

    }

    protected void deleteSite() {
        boolean result = MessageDialog.openConfirm(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), "Site Deletion",
            "Are you sure you want to delete site " + site.getName() + "?");

        if (!result)
            return;

        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            public void run() {
                try {
                    SDKQuery query;

                    WritableApplicationService appService = getAppService();
                    query = new DeleteExampleQuery(site);
                    site.getAddress();
                    site.getClinicCollection();
                    // FIXME should delete studies - patients - patient visits -
                    // sample !
                    appService.executeQuery(query);
                    getParent().removeChild(SiteAdapter.this);
                } catch (final RemoteAccessException exp) {
                    BioBankPlugin.openRemoteAccessErrorMessage();
                } catch (Exception e) {
                    SessionManager.getLogger().error(
                        "Error while deletindg site " + site.getName());
                }
            }
        });
    }

    @Override
    public AdapterBase accept(NodeSearchVisitor visitor) {
        return visitor.visit(this);
    }

}