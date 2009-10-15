package edu.ualberta.med.biobank.treeview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.springframework.remoting.RemoteAccessException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SessionAdapter extends AdapterBase {

    private static Logger LOGGER = Logger.getLogger(SessionAdapter.class
        .getName());

    private WritableApplicationService appService;

    private String userName;

    public SessionAdapter(AdapterBase parent,
        WritableApplicationService appService, int sessionId, String name,
        String userName) {
        super(parent, null);
        this.appService = appService;
        setId(sessionId);
        setName(name);
        this.userName = userName;
        // getUserCsmId();
    }

    @Override
    public WritableApplicationService getAppService() {
        return appService;
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public void performDoubleClick() {
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        MenuItem mi = new MenuItem(menu, SWT.PUSH);
        mi.setText("Add Repository Site");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                IHandlerService handlerService = (IHandlerService) PlatformUI
                    .getWorkbench().getService(IHandlerService.class);

                try {
                    handlerService.executeCommand(
                        "edu.ualberta.med.biobank.commands.siteAdd", null);
                } catch (Exception ex) {
                    throw new RuntimeException(
                        "edu.ualberta.med.biobank.commands.addSite not found");
                }
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        mi = new MenuItem(menu, SWT.PUSH);
        mi.setText("Logout");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                IHandlerService handlerService = (IHandlerService) PlatformUI
                    .getWorkbench().getService(IHandlerService.class);

                try {
                    handlerService.executeCommand(
                        "edu.ualberta.med.biobank.commands.logout", null);
                } catch (Exception ex) {
                    throw new RuntimeException(
                        "edu.ualberta.med.biobank.commands.logout not found");
                }
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    @Override
    public void loadChildren(boolean updateNode) {
        try {
            // read from database again
            Integer siteId = null;
            SiteWrapper currentSite = SessionManager.getInstance()
                .getCurrentSiteWrapper();
            if (currentSite != null)
                siteId = currentSite.getId();

            List<SiteWrapper> siteCollection = new ArrayList<SiteWrapper>(
                SiteWrapper.getSites(appService, siteId));
            Collections.sort(siteCollection);

            for (SiteWrapper siteWrapper : siteCollection) {
                SiteAdapter node = (SiteAdapter) getChild(siteWrapper.getId());
                if (node == null) {
                    node = new SiteAdapter(this, siteWrapper);
                    addChild(node);
                }
                if (updateNode) {
                    SessionManager.getInstance().updateTreeNode(node);
                }
            }
        } catch (final RemoteAccessException exp) {
            BioBankPlugin.openRemoteAccessErrorMessage();
        } catch (Exception e) {
            LOGGER.error("Error while loading sites for session " + getName(),
                e);
        }
    }

    @Override
    public AdapterBase accept(NodeSearchVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public String getTreeText() {
        if (userName.isEmpty()) {
            return super.getTreeText();
        } else {
            return super.getTreeText() + " [" + userName + "]";
        }

    }

    public String getUserName() {
        return userName;
    }

}
