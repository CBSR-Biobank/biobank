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

import edu.ualberta.med.biobank.forms.SiteEntryForm;
import edu.ualberta.med.biobank.forms.SiteViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.example.DeleteExampleQuery;

public class SiteAdapter extends Node {
    public static final int STUDIES_NODE_ID = 0;
    public static final int CLINICS_NODE_ID = 1;
    public static final int STORAGE_TYPES_NODE_ID = 2;
    
	private Site site;

	public SiteAdapter(SessionAdapter parent, Site site) {
		super(parent);
		this.site = site;
		addChild(new StudyGroup(this, STUDIES_NODE_ID));
		addChild(new ClinicGroup(this, CLINICS_NODE_ID));
		addChild(new StorageTypeGroup(this, STORAGE_TYPES_NODE_ID));
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public Site getSite() {
		return site;
	}
	
	public Node getStudiesGroupNode() {
		return children.get(STUDIES_NODE_ID);
	}
	
	public Node getClinicGroupNode() {
		return children.get(CLINICS_NODE_ID);
	}
    
    public Node getStorageTypesGroupNode() {
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
	public void performDoubleClick() {
        openForm(new FormInput(this), SiteViewForm.ID);
    }
    
    @Override
	public void performExpand() {
    }
    
    @Override
	public void popupMenu(TreeViewer tv, Tree tree,  Menu menu) {
        MenuItem mi = new MenuItem (menu, SWT.PUSH);
        mi.setText ("Edit Site");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                openForm(new FormInput(SiteAdapter.this), SiteEntryForm.ID);
            }

            public void widgetDefaultSelected(SelectionEvent e) {                    
            }
        });

        mi = new MenuItem (menu, SWT.PUSH);
        mi.setText ("View Site");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                openForm(new FormInput(SiteAdapter.this), SiteViewForm.ID);
            }

            public void widgetDefaultSelected(SelectionEvent e) {                    
            }
        }); 

        mi = new MenuItem (menu, SWT.PUSH);
        mi.setText ("Delete Site");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                deleteSite();
            }

            public void widgetDefaultSelected(SelectionEvent e) {                    
            }
        }); 
    }
    
    protected void deleteSite() {
        boolean result = MessageDialog.openConfirm(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
            "Site Deletion", 
            "Are you sure you want to delete site " + site.getName()
            + "?");
        
        if (!result) return;
        
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            public void run() {

                try {
                    SDKQuery query;

                    WritableApplicationService appService = getAppService();       
                    query = new DeleteExampleQuery(site);
                    site.getAddress();
                    site.getClinicCollection();
                    appService.executeQuery(query);
                }
                catch (final RemoteAccessException exp) {
                    Display.getDefault().asyncExec(new Runnable() {
                        public void run() {
                            MessageDialog.openError(
                                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
                                "Connection Attempt Failed", 
                            "Could not perform database operation. Make sure server is running correct version.");
                        }
                    });
                }
                catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }
}
