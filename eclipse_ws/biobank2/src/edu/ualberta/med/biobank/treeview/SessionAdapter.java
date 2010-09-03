package edu.ualberta.med.biobank.treeview;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

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

public class SessionAdapter extends AdapterBase {

    private static final String LOGOUT_COMMAND_ID = "edu.ualberta.med.biobank.commands.logout";

    public static final int CLINICS_BASE_NODE_ID = 0;

    public static final int SITES_NODE_ID = 1;

    public static final int STUDIES_NODE_ID = 2;

    private WritableApplicationService appService;

    private String userName;
    private String serverName;

    public SessionAdapter(AdapterBase parent,
        WritableApplicationService appService, int sessionId,
        String serverName, String userName) {
        super(parent, null, false);
        this.appService = appService;
        setId(sessionId);
        if (userName != null && userName.isEmpty()) {
            setName(serverName);
        } else {
            setName(serverName + " [" + userName + "]");
        }
        this.serverName = serverName;
        this.userName = userName;

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
    public WritableApplicationService getAppService() {
        return appService;
    }

    @Override
    protected String getLabelInternal() {
        return "";
    }

    @Override
    public String getTooltipText() {
        return "";
    }

    public AdapterBase getSitesGroupNode() {
        AdapterBase adapter = getChild(SITES_NODE_ID);
        Assert.isNotNull(adapter);
        return adapter;
    }

    public AdapterBase getStudiesGroupNode() {
        AdapterBase adapter = getChild(STUDIES_NODE_ID);
        Assert.isNotNull(adapter);
        return adapter;
    }

    public AdapterBase getClinicGroupNode() {
        AdapterBase adapter = getChild(CLINICS_BASE_NODE_ID);
        Assert.isNotNull(adapter);
        return adapter;
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

    public String getUserName() {
        return userName;
    }

    public String getServerName() {
        return serverName;
    }

    @Override
    public AdapterBase accept(NodeSearchVisitor visitor) {
        return visitor.visit(this);
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
