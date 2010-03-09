package edu.ualberta.med.biobank.treeview;

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

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SessionAdapter extends AdapterBase {

    private WritableApplicationService appService;

    private String userName;

    private static final String ADDSITE_COMMAND_ID = "edu.ualberta.med.biobank.commands.siteAdd";

    private static final String LOGOUT_COMMAND_ID = "edu.ualberta.med.biobank.commands.logout";

    public SessionAdapter(AdapterBase parent,
        WritableApplicationService appService, int sessionId, String name,
        String userName) {
        super(parent, null, true, false);
        this.appService = appService;
        setId(sessionId);
        setName(name);
        this.userName = userName;
    }

    @Override
    public WritableApplicationService getAppService() {
        return appService;
    }

    @Override
    protected String getLabelInternal() {
        if (userName.isEmpty()) {
            return super.getLabel();
        } else {
            return super.getLabel() + " [" + userName + "]";
        }
    }

    @Override
    public String getTooltipText() {
        return "";
    }

    @Override
    public void executeDoubleClick() {
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
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
                    throw new RuntimeException(ADDSITE_COMMAND_ID + " not found");
                }
            }
        });

        mi = new MenuItem(menu, SWT.PUSH);
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
        SiteWrapper currentSite = SessionManager.getInstance()
            .getCurrentSiteWrapper();
        Integer siteId = null;
        if (currentSite != null) {
            siteId = currentSite.getId();
        }
        return new ArrayList<SiteWrapper>(SiteWrapper.getSites(appService,
            siteId));
    }

}
