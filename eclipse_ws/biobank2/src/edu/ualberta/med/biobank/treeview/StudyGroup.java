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

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.StudyEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class StudyGroup extends Node {
    
    private static Logger log4j = Logger.getLogger(SessionManager.class.getName());

    public StudyGroup(SiteAdapter parent, int id) {
        super(parent, id, "Studies", true);
    }
    
    public void openViewForm() throws PartInitException {
        Assert.isTrue(false, "should not be called");
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

                    Collection<Study> studies = site.getStudyCollection();
                    currentSite.setStudyCollection(studies);
                    log4j.trace("updateStudies: Site " 
                            + site.getName() + " has " + studies.size() + " studies");

                    for (Study study: studies) {
                        log4j.trace("updateStudies: Study "
                                + study.getId() + ": " + study.getName()
                                + ", short name: " + study.getNameShort());
                        
                        StudyAdapter node = 
                            new StudyAdapter(StudyGroup.this, study);
                        addChild(node);
                        SessionManager.getInstance().getTreeViewer().update(node, null);
                    }
                    SessionManager.getInstance().getTreeViewer().expandToLevel(
                        StudyGroup.this, 1);
                }
                catch (ApplicationException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    public void popupMenu(TreeViewer tv, Tree tree,  Menu menu) {
        MenuItem mi = new MenuItem (menu, SWT.PUSH);
        mi.setText ("Add Study");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                StudyAdapter adapter = new StudyAdapter(
                    StudyGroup.this, new Study());
                openForm(new FormInput(adapter), StudyEntryForm.ID);
            }

            public void widgetDefaultSelected(SelectionEvent e) {                    
            }
        });
    }

}
