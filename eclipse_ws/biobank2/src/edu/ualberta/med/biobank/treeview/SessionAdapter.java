package edu.ualberta.med.biobank.treeview;

import java.util.List;

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
import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SessionAdapter extends AdapterBase {

    private WritableApplicationService appService;

    private String userName;

    public SessionAdapter(AdapterBase parent,
        WritableApplicationService appService, int sessionId, String name,
        String userName) {
        super(parent);
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
    public Integer getId() {
        return null;
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
                    // throw new
                    // RuntimeException("edu.ualberta.med.biobank.commands.addSite not found");
                    ex.printStackTrace();
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
            Site siteSearch = new Site();
            List<Site> result = appService.search(Site.class, siteSearch);
            for (Site site : result) {
                SessionManager.getLogger()
                    .trace(
                        "updateSites: Site " + site.getId() + ": "
                            + site.getName());

                SiteAdapter node = (SiteAdapter) getChild(site.getId());
                if (node == null) {
                    node = new SiteAdapter(this, site);
                    addChild(node);
                }
                if (updateNode) {
                    SessionManager.getInstance().getTreeViewer().update(node,
                        null);
                }
            }
        } catch (final RemoteAccessException exp) {
            BioBankPlugin.openRemoteAccessErrorMessage();
        } catch (Exception e) {
            SessionManager.getLogger().error(
                "Error while loading sites for session " + getName());
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

    public String getUserCsmId() {
        HQLCriteria criteria = new HQLCriteria(
            "from gov.nih.nci.security.authorization.domainobjects.User where loginName = '"
                + userName + "'");
        try {
            List<Object> userCsmId = appService.query(criteria);
            System.out.println(userCsmId);
        } catch (ApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }
}