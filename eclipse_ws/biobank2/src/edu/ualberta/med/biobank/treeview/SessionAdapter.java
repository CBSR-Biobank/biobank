package edu.ualberta.med.biobank.treeview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SessionAdapter extends AdapterBase {

    private static final String LOGOUT_COMMAND_ID = "edu.ualberta.med.biobank.commands.logout";

    public static final int CLINICS_BASE_NODE_ID = 0;

    public static final int SITES_NODE_ID = 1;

    public static final int STUDIES_NODE_ID = 2;

    private BiobankApplicationService appService;

    private User user;
    private String serverName;

    public SessionAdapter(AdapterBase parent,
        BiobankApplicationService appService, int sessionId, String serverName,
        User user) {
        super(parent, null, false);
        this.appService = appService;
        setId(sessionId);
        if (user.getLogin().isEmpty()) {
            setName(serverName);
        } else {
            setName(serverName + " [" + user.getLogin() + "]");
        }
        this.serverName = serverName;
        this.user = user;

        addGroupNodes();
    }

    private void addGroupNodes() {
        addChild(new ClinicMasterGroup(this, CLINICS_BASE_NODE_ID));
        addChild(new StudyMasterGroup(this, STUDIES_NODE_ID));
        addChild(new SiteGroup(this, SITES_NODE_ID));
    }

    @Override
    public void rebuild() {
        for (AdapterBase child : new ArrayList<AdapterBase>(getChildren())) {
            child.rebuild();
        }
    }

    @Override
    public BiobankApplicationService getAppService() {
        return appService;
    }

    public void resetAppService() {
        appService = null;
    }

    @Override
    protected String getLabelInternal() {
        return "";
    }

    @Override
    public String getTooltipText() {
        return "";
    }

    public SiteGroup getSitesGroupNode() {
        AdapterBase adapter = getChild(SITES_NODE_ID);
        Assert.isNotNull(adapter);
        return (SiteGroup) adapter;
    }

    public StudyMasterGroup getStudiesGroupNode() {
        AdapterBase adapter = getChild(STUDIES_NODE_ID);
        Assert.isNotNull(adapter);
        return (StudyMasterGroup) adapter;
    }

    public ClinicMasterGroup getClinicGroupNode() {
        AdapterBase adapter = getChild(CLINICS_BASE_NODE_ID);
        Assert.isNotNull(adapter);
        return (ClinicMasterGroup) adapter;
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        MenuItem mi = new MenuItem(menu, SWT.PUSH);
        mi.setText("Logout");
        mi.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                IHandlerService handlerService = (IHandlerService) PlatformUI
                    .getWorkbench().getService(IHandlerService.class);

                try {
                    handlerService.executeCommand(LOGOUT_COMMAND_ID, null);
                } catch (Exception ex) {
                    throw new RuntimeException(LOGOUT_COMMAND_ID + " not found");
                }
            }
        });
    }

    public User getUser() {
        return user;
    }

    public String getServerName() {
        return serverName;
    }

    @Override
    public AdapterBase search(Object searchedObject) {
        return searchChildren(searchedObject);
    }

    @Override
    protected AdapterBase createChildNode() {
        return new SiteAdapter(this, null);
    }

    @Override
    protected AdapterBase createChildNode(ModelWrapper<?> child) {
        return null;
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren() {
        return null;
    }

    @Override
    protected int getWrapperChildCount() {
        return 0;
    }

    @Override
    public String getEntryFormId() {
        return null;
    }

    @Override
    public String getViewFormId() {
        return null;
    }

    @SuppressWarnings("unused")
    public List<ClinicWrapper> getClinicCollection(boolean sort) {
        try {
            return ClinicWrapper.getAllClinics(appService);
        } catch (ApplicationException e) {
            BioBankPlugin.openAsyncError(
                "Unable to load clinics from database", e);
        }
        return null;
    }
}
