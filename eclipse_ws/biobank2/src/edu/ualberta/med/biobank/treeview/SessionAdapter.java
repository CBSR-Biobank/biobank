package edu.ualberta.med.biobank.treeview;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.springframework.remoting.RemoteAccessException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SessionAdapter extends Node {
    
    private static Logger log4j = Logger.getLogger(SessionManager.class.getName());
	
	private WritableApplicationService appService;
	
	public SessionAdapter(Node parent, WritableApplicationService appService, 
			int sessionId, String name) {
		super(parent);
		this.appService = appService;
		setId(sessionId);
		setName(name);
	}

	@Override
	public WritableApplicationService getAppService() {
		return appService;
	}

	@Override
	public int getId() {
		return 0;
	}

    @Override
	public void performExpand() {        
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {                
                // read from database again 
                Site siteSearch = new Site();    
                
                WritableApplicationService appService = getAppService();
                try {
                    List<Site> result = appService.search(Site.class, siteSearch);
                    for (Site site: result) {
                        log4j.trace("updateSites: Site "
                                + site.getId() + ": " + site.getName());
                        
                        SiteAdapter node =  (SiteAdapter) getChild(site.getId());
                        if (node == null) {
                            node = new SiteAdapter(SessionAdapter.this, site);
                            addChild(node);
                        }
                        SessionManager.getInstance().getTreeViewer().update(node, null);                        
                    }
                    SessionManager.getInstance().getTreeViewer().expandToLevel(
                        SessionAdapter.this, 1);
                }
                catch (final RemoteAccessException exp) {
                	BioBankPlugin.openRemoteAccessErrorMessage();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    @Override
	public void popupMenu(TreeViewer tv, Tree tree,  Menu menu) {
        MenuItem mi = new MenuItem (menu, SWT.PUSH);
        mi.setText ("Logout");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                IHandlerService handlerService = 
                    (IHandlerService) PlatformUI.getWorkbench().getService(
                        IHandlerService.class);

                try {
                    handlerService.executeCommand("edu.ualberta.med.biobank.commands.logout", null);
                } catch (Exception ex) {
                    throw new RuntimeException("edu.ualberta.med.biobank.commands.logout not found");
                }
            }

            public void widgetDefaultSelected(SelectionEvent e) {                    
            }
        });

        mi = new MenuItem (menu, SWT.PUSH);
        mi.setText ("Add Repository Site");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                IHandlerService handlerService = 
                    (IHandlerService) PlatformUI.getWorkbench().getService(
                        IHandlerService.class);

                try {
                    handlerService.executeCommand("edu.ualberta.med.biobank.commands.addSite", null);
                } catch (Exception ex) {
                    //throw new RuntimeException("edu.ualberta.med.biobank.commands.addSite not found");
                    ex.printStackTrace();
                }
            }

            public void widgetDefaultSelected(SelectionEvent e) {                    
            }
        });
    }
}
