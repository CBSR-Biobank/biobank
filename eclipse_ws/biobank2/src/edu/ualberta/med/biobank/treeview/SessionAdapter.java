package edu.ualberta.med.biobank.treeview;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.util.ArrayList;
import java.util.Collection;

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

    private static final String ADDSITE_COMMAND_ID = "edu.ualberta.med.biobank.commands.siteAdd";

    private static final String LOGOUT_COMMAND_ID = "edu.ualberta.med.biobank.commands.logout";

    public static final int STUDIES_BASE_NODE_ID = 1;

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

        addChild(new StudyGroup(this, STUDIES_BASE_NODE_ID));
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

    public AdapterBase getStudiesGroupNode() {
        return children.get(STUDIES_BASE_NODE_ID);
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        if (SessionManager.canCreate(SiteWrapper.class)) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText("Add Repository Site");
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    IHandlerService handlerService = (IHandlerService) PlatformUI
                        .getWorkbench().getService(IHandlerService.class);

                    try {
                        handlerService.executeCommand(ADDSITE_COMMAND_ID, null);
                    } catch (Exception ex) {
                        throw new RuntimeException(ADDSITE_COMMAND_ID
                            + " not found");

                    }
                }
            });
        }

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
        Assert.isTrue(child instanceof SiteWrapper);
        return new SiteAdapter(this, (SiteWrapper) child);
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        Integer siteId = null;
        if (!SessionManager.getInstance().isAllSitesSelected()) {
            SiteWrapper currentSite = SessionManager.getInstance()
                .getCurrentSite();
            siteId = currentSite.getId();
        }
        return new ArrayList<SiteWrapper>(SiteWrapper.getSites(appService,
            siteId));
    }

    @Override
    protected int getWrapperChildCount() throws Exception {
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

}
