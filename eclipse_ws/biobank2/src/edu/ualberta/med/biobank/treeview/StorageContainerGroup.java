package edu.ualberta.med.biobank.treeview;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.StorageContainerEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.StorageContainer;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class StorageContainerGroup extends Node {

    public StorageContainerGroup(SiteAdapter parent, int id) {
        super(parent, id, "Storage Containers", true);
    }

    @Override
	public void performDoubleClick() {
        performExpand();
    }

    @Override
	public void performExpand() {   
        final Site parentSite = ((SiteAdapter) getParent()).getSite();
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {                
                // read from database again                 
                WritableApplicationService appService = getAppService();
                try {
                    Site searchStudy = new Site();
                    searchStudy.setId(parentSite.getId());
                    List<Site> result = appService.search(Site.class, searchStudy);
                    Assert.isTrue(result.size() == 1);
                    searchStudy = result.get(0);

                    Collection<StorageContainer> patients = 
                        searchStudy.getStorageContainerCollection();

                    for (StorageContainer patient: patients) {
                        StorageContainerAdapter node = 
                            (StorageContainerAdapter) getChild(patient.getId());

                        if (node == null) {
                            node = new StorageContainerAdapter(
                                StorageContainerGroup.this, patient);
                            addChild(node);
                        }
                        
                        SessionManager.getInstance().getTreeViewer().update(node, null);
                    }
                    SessionManager.getInstance().getTreeViewer().expandToLevel(
                        StorageContainerGroup.this, 1);
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
        mi.setText ("Add a Storage Container");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                StorageContainerAdapter adapter = new StorageContainerAdapter(
                    StorageContainerGroup.this, new StorageContainer());
                openForm(new FormInput(adapter), StorageContainerEntryForm.ID);
            }

            public void widgetDefaultSelected(SelectionEvent e) {                    
            }
        }); 
    }

}
