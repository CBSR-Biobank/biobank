package edu.ualberta.med.biobank;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import edu.ualberta.med.biobank.forms.ClinicViewForm;
import edu.ualberta.med.biobank.forms.SiteEntryForm;
import edu.ualberta.med.biobank.forms.SiteViewForm;
import edu.ualberta.med.biobank.forms.NodeInput;
import edu.ualberta.med.biobank.forms.StudyEntryForm;
import edu.ualberta.med.biobank.forms.StudyViewForm;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.ClinicAdapter;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.SessionAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;
import edu.ualberta.med.biobank.views.SessionsView;

public class SessionManager {
	private static SessionManager instance = null;
	
	static Logger log4j = Logger.getLogger(SessionManager.class.getName());
	
	private SessionsView view;
	
	private HashMap<String, SessionAdapter> sessions;
	
	private Node rootNode;
	
	public Node getRootNode() {
		return rootNode;
	}

	private IDoubleClickListener doubleClickListener = new IDoubleClickListener() {
		public void doubleClick(DoubleClickEvent event) {
			Object selection = event.getSelection();

			if (selection == null) return;

			Object element = ((StructuredSelection)selection).getFirstElement();

			view.getTreeViewer().expandToLevel(element, 1);

			if (element instanceof SiteAdapter) {
				openSiteViewForm((SiteAdapter) element);
			}
            else if (element instanceof StudyAdapter) {
                openStudyViewForm((StudyAdapter) element);
            }
			else if (element instanceof ClinicAdapter) {
				openClinicViewForm((ClinicAdapter) element);
			}
			else if (element instanceof Node) {
				Node node = (Node) element;
                if (node.getName().equals("Studies")) {
                    updateStudies(node);
                }
                else if (node.getName().equals("Clinics")) {
					updateClinics(node);
				}
			}
			else {
				Assert.isTrue(false, "double click on class "
						+ element.getClass().getName() + " not implemented yet");
			}
		}
	};
	
	public IDoubleClickListener getDoubleClickListener() {
		return doubleClickListener;
	}

	private ITreeViewerListener treeViewerListener = new ITreeViewerListener() {
		@Override
		public void treeCollapsed(TreeExpansionEvent e) {
		}

		@Override
		public void treeExpanded(TreeExpansionEvent e) {
			Object o = e.getElement();
			if (o instanceof Node) {
				Node node = (Node) o;
				if (node.getName().equals("Studies")) {
					updateStudies(node);
				}			
				else if (node.getName().equals("Clinics")) {
                    updateClinics(node);
                }           
			}
		}
	};
	
	private Listener treeViewMenuListener = new Listener() {
        @Override
        public void handleEvent(Event event) {
            TreeViewer tv = view.getTreeViewer();
            Tree tree = tv.getTree();
            Menu menu = tree.getMenu();
            
            for (MenuItem menuItem : menu.getItems ()) {
                menuItem.dispose ();
            }
            
            Object element = ((StructuredSelection)
                    tv.getSelection()).getFirstElement();

            if (element instanceof SessionAdapter) {
                popupMenuSessionNode((SessionAdapter) element, tv, tree, menu);
            }
            else if (element instanceof SiteAdapter) {
                popupMenuSiteNode((SiteAdapter) element, tv, tree, menu);
            }
            else if (element instanceof StudyAdapter) {
                popupMenuStudyNode((StudyAdapter) element, tv, tree, menu);
            }
            else if (element instanceof ClinicAdapter) {
            }
            else if (element instanceof Node) {
                Node node = (Node) element;
                if (node.getName().equals("Studies")) {
                }
                else if (node.getName().equals("Clinics")) {
                }
                else if (node.getName().equals("Storage Container")) {
                }
                else {
                    Assert.isTrue(false, "double click on class "
                            + node.getName() + " is not supported");
                }
            }
            else {
                Assert.isTrue(false, "double click on class "
                        + element.getClass().getName() + " is not supported");
            }
        }
	};
	
	public ITreeViewerListener getTreeViewerListener() {
		return treeViewerListener;
	}
	
	public Listener getTreeViewerMenuListener() {
	    return treeViewMenuListener;
	}

	public static SessionManager getInstance() {
		if (instance == null) {
			instance = new SessionManager();
		}
		return instance;
	}
	
	private SessionManager() {
		super();
		rootNode = new Node(null, 1, "root");
		sessions = new  HashMap<String, SessionAdapter>();
	}
	
	public void setSessionsView(SessionsView view) {
		this.view = view;
	}
	
	public void addSession(final WritableApplicationService appService, String name, 
			List<Site> sites) {
		int id = sessions.size();
		final SessionAdapter sessionNode = new SessionAdapter(rootNode, appService, id, name);
		sessions.put(name, sessionNode);
		rootNode.addChild(sessionNode);
		
		for (Object o : sites) {
			SiteAdapter siteNode = new SiteAdapter(sessionNode, (Site) o);
			sessionNode.addChild(siteNode);
		}
		view.getTreeViewer().expandToLevel(2);	
		log4j.debug("addSession: " + name);
	}
	
	public SessionAdapter getSessionAdapter(String sessionName) {
		for (Node node : rootNode.getChildren()) {
			if (node.getName().equals(sessionName)) 
				return (SessionAdapter) node;
		}
		Assert.isTrue(false, "Session with name " + sessionName
				+ " not found");
		return null;
	}
	
	public SessionAdapter getSessionAdapter(int count) {
		List<Node> nodes = rootNode.getChildren();
		Assert.isTrue(count < nodes.size(), 
				"Invalid session node count: " + count);
		return (SessionAdapter) nodes.get(count);
	}
    
    public void updateSites(final SessionAdapter sessionAdapter) {        
        view.getTreeViewer().getControl().getDisplay().asyncExec(new Runnable() {
            public void run() {                
                // read from database again 
                Site siteSearch = new Site();    
                
                WritableApplicationService appService = sessionAdapter.getAppService();
                try {
                    List<Site> result = appService.search(Site.class, siteSearch);
                    for (Site site: result) {
                        SessionManager.log4j.trace("updateSites: Site "
                                + site.getId() + ": " + site.getName());
                        
                        SiteAdapter node = new SiteAdapter(sessionAdapter, site);
                        sessionAdapter.addChild(node);
                        view.getTreeViewer().update(node, null);
                    }
                    view.getTreeViewer().expandToLevel(sessionAdapter, 1);
                }
                catch (ApplicationException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    public void updateStudies(final Node groupNode) {       
        final Site currentSite = ((SiteAdapter) groupNode.getParent()).getSite();
        Assert.isNotNull(currentSite, "null site");        
        
        view.getTreeViewer().getControl().getDisplay().asyncExec(new Runnable() {
            public void run() {                
                // read from database again 
                Site site = new Site();                
                site.setId(currentSite.getId());
                
                WritableApplicationService appService = groupNode.getAppService();
                try {
                    List<Site> result = appService.search(Site.class, site);
                    Assert.isTrue(result.size() == 1);
                    site = result.get(0);

                    Collection<Study> studies = site.getStudyCollection();
                    currentSite.setStudyCollection(studies);
                    SessionManager.log4j.trace("updateStudies: Site " 
                            + site.getName() + " has " + studies.size() + " studies");

                    for (Study study: studies) {
                        SessionManager.log4j.trace("updateStudies: Study "
                                + study.getId() + ": " + study.getName()
                                + ", short name: " + study.getNameShort());
                        
                        StudyAdapter node = new StudyAdapter(groupNode, study);
                        groupNode.addChild(node);
                        view.getTreeViewer().update(node, null);
                    }
                    view.getTreeViewer().expandToLevel(groupNode, 1);
                }
                catch (ApplicationException e) {
                    e.printStackTrace();
                }
            }
        });
    }
	
    public void updateClinics(final Node groupNode) {	     
        final Site currentSite = ((SiteAdapter) groupNode.getParent()).getSite();
        Assert.isNotNull(currentSite, "null site");   

        view.getTreeViewer().getControl().getDisplay().asyncExec(new Runnable() {
            public void run() {              
                // read from database again 
                Site site = new Site();                
                site.setId(currentSite.getId());

                WritableApplicationService appService = groupNode.getAppService();
                try {
                    List<Site> result = appService.search(Site.class, site);
                    Assert.isTrue(result.size() == 1);
                    site = result.get(0);
                    
                    Collection<Clinic> clinics = site.getClinicCollection();
                    currentSite.setClinicCollection(clinics);
                    SessionManager.log4j.trace("updateStudies: Site " 
                            + site.getName() + " has " + clinics.size() + " studies");

                    for (Clinic clinic : clinics) {
                        SessionManager.log4j.trace("updateStudies: Study "
                                + clinic.getId() + ": " + clinic.getName());
                        
                        ClinicAdapter node = new ClinicAdapter(groupNode, clinic);
                        groupNode.addChild(node);
                    }
                    view.getTreeViewer().expandToLevel(groupNode, 1);
                }
                catch (ApplicationException e) {
                    e.printStackTrace();
                }
            }
        });
    }
	
	public void deleteSession(String name) {
		rootNode.removeByName(name);
		//treeViewer.refresh();
	}
	
	public int getSessionCount() {
		return rootNode.getChildren().size();
	}
	
	public String[] getSessionNames() {
		return sessions.keySet().toArray(new String[sessions.size()]);
	}
	
	private void openSiteViewForm(SiteAdapter node) {
		NodeInput input = new NodeInput(node);
		
		try {
			view.getSite().getPage().openEditor(input, SiteViewForm.ID, true);
		} 
		catch (PartInitException e) {
			// handle error
			e.printStackTrace();				
		}
	}
    
    public void openStudyViewForm(StudyAdapter node) {
        NodeInput input = new NodeInput(node);
        
        try {
            view.getSite().getPage().openEditor(input, StudyViewForm.ID, true);
        } 
        catch (PartInitException e) {
            e.printStackTrace();                
        }
    }
	
	public void openClinicViewForm(ClinicAdapter node) {
		NodeInput input = new NodeInput(node);
		
		try {
			view.getSite().getPage().openEditor(input, ClinicViewForm.ID, true);
		} 
		catch (PartInitException e) {
			e.printStackTrace();				
		}
	}
	
	public SessionAdapter getSessionSingle() {
		int count = sessions.size();
		Assert.isTrue(count == 1, "No sessions or more than 1 session connected");
		return getSessionAdapter(0);
	}
	
	public TreeViewer getTreeViewer() {
	    return view.getTreeViewer();
	}
	
	private void popupMenuSessionNode(SessionAdapter siteAdapter, TreeViewer tv,  
	        Tree tree,  Menu menu) {
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
        mi.setText ("Add Site");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                IHandlerService handlerService = 
                    (IHandlerService) PlatformUI.getWorkbench().getService(
                        IHandlerService.class);

                try {
                    handlerService.executeCommand("edu.ualberta.med.biobank.commands.addSite", null);
                } catch (Exception ex) {
                    throw new RuntimeException("edu.ualberta.med.biobank.commands.addSite not found");
                }
            }

            public void widgetDefaultSelected(SelectionEvent e) {                    
            }
        });
	    
	}
	
	private void closeEditor(NodeInput input) {
        IEditorPart part = 
            view.getSite().getPage().findEditor(input);
        if (part != null) {
            view.getSite().getPage().closeEditor(part, true);
        }
	    
	}
    
    private void popupMenuSiteNode(final SiteAdapter siteAdapter, TreeViewer tv,  
            Tree tree,  Menu menu) {
        MenuItem mi = new MenuItem (menu, SWT.PUSH);
        mi.setText ("Edit Site");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                NodeInput ni = new NodeInput(siteAdapter);
                closeEditor(ni);
                try {
                    view.getSite().getPage().openEditor(ni, SiteEntryForm.ID, true);
                }
                catch (PartInitException exp) {
                    exp.printStackTrace();              
                }
            }

            public void widgetDefaultSelected(SelectionEvent e) {                    
            }
        });

        mi = new MenuItem (menu, SWT.PUSH);
        mi.setText ("View Site");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                closeEditor(new NodeInput(siteAdapter));
                openSiteViewForm(siteAdapter);
            }

            public void widgetDefaultSelected(SelectionEvent e) {                    
            }
        }); 
    }
    
    private void popupMenuStudyNode(final StudyAdapter studyAdapter, TreeViewer tv,  
            Tree tree,  Menu menu) {
        MenuItem mi = new MenuItem (menu, SWT.PUSH);
        mi.setText ("Edit Study");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                NodeInput ni = new NodeInput(studyAdapter);
                closeEditor(ni);
                try {
                    view.getSite().getPage().openEditor(ni, StudyEntryForm.ID, true);
                }
                catch (PartInitException exp) {
                    exp.printStackTrace();              
                }
            }

            public void widgetDefaultSelected(SelectionEvent e) {                    
            }
        });

        mi = new MenuItem (menu, SWT.PUSH);
        mi.setText ("View Study");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                closeEditor(new NodeInput(studyAdapter));
                openStudyViewForm(studyAdapter);
            }

            public void widgetDefaultSelected(SelectionEvent e) {                    
            }
        }); 
    }
}
