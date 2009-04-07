package edu.ualberta.med.biobank.treeview;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.StorageTypeEntryForm;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.StorageType;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

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

public class StorageTypeGroup extends Node {
    
    private static Logger log4j = Logger.getLogger(SessionManager.class.getName());

    public StorageTypeGroup(SiteAdapter parent, int id) {
        super(parent, id, "Storage Types", true);
    }
    
    public void performDoubleClick() {
        performExpand();
    }

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

                    Collection<StorageType> storageTypes = 
                        site.getStorageTypeCollection();
                    currentSite.setStorageTypeCollection(storageTypes);
                    log4j.trace("updateStudies: Site " 
                        + site.getName() + " has " + storageTypes.size() 
                        + " studies");

                    for (StorageType storageType : storageTypes) {
                        log4j.trace(
                            "updateStudies: Storage Type "
                            + storageType.getId() + ": " + storageType.getName());

                        StorageTypeAdapter node = new StorageTypeAdapter(
                            StorageTypeGroup.this, storageType);
                        addChild(node);
                    }
                    SessionManager.getInstance().getTreeViewer().expandToLevel(
                        StorageTypeGroup.this, 1);
                }
                catch (ApplicationException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    public void popupMenu(TreeViewer tv, Tree tree,  Menu menu) {
        MenuItem mi = new MenuItem (menu, SWT.PUSH);
        mi.setText ("Add Storage Type");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                openForm(StorageTypeEntryForm.ID);
            }

            public void widgetDefaultSelected(SelectionEvent e) {                    
            }
        });
    }

}
