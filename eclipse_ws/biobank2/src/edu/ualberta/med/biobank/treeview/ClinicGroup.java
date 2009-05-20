package edu.ualberta.med.biobank.treeview;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.ClinicEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ClinicGroup extends Node {
    
    private static Logger log4j = Logger.getLogger(SessionManager.class.getName());

    public ClinicGroup(SiteAdapter parent, int id) {
        super(parent, id, "Clinics", true);
    }

    @Override
	public void performDoubleClick() {
        performExpand();
    }

    @Override
	public void performExpand() {    
        final Site currentSite = ((SiteAdapter) getParent()).getSite();
        Assert.isNotNull(currentSite, "null site");   

        Display.getDefault().asyncExec(new Runnable() {
            public void run() {              
                // read from database again 
                Site site = new Site();                
                site.setId(currentSite.getId());

                WritableApplicationService appService = getAppService();
                try {
                    List<Site> result = appService.search(Site.class, site);
                    Assert.isTrue(result.size() == 1);
                    site = result.get(0);
                    
                    Collection<Clinic> clinics = site.getClinicCollection();
                    currentSite.setClinicCollection(clinics);
                    log4j.trace("updateStudies: Site " 
                            + site.getName() + " has " + clinics.size() + " studies");

                    for (Clinic clinic : clinics) {
                        log4j.trace("updateStudies: Clinic "
                                + clinic.getId() + ": " + clinic.getName());
                        
                        ClinicAdapter node = 
                            (ClinicAdapter) getChild(clinic.getId());

                        if (node == null) {
                            node = new ClinicAdapter(ClinicGroup.this, clinic);
                            addChild(node);
                        }
                        
                        SessionManager.getInstance().getTreeViewer().update(node, null);
                    }
                    SessionManager.getInstance().getTreeViewer().expandToLevel(
                        ClinicGroup.this, 1);
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
        mi.setText ("Add Clinic");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                ClinicAdapter clinicAdapter = new ClinicAdapter(
                    ClinicGroup.this, new Clinic());
                FormInput input = new FormInput(clinicAdapter);
                try {
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                    .openEditor(input, ClinicEntryForm.ID, true);
                }
                catch (PartInitException exp) {
                    exp.printStackTrace();              
                }
            }

            public void widgetDefaultSelected(SelectionEvent e) {                    
            }
        });
    }
}
