package edu.ualberta.med.biobank.treeview;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.forms.SiteEntryForm;
import edu.ualberta.med.biobank.forms.SiteViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Site;

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
	public int getId() {
		Object o = (Object) site.getId();
		if (o == null) return 0;
		return site.getId();
	}

	@Override
	public String getName() {
		Object o = (Object) site.getName();
		if (o == null) return null;
		return site.getName();
	}
    
    public void performDoubleClick() {
        openForm(new FormInput(this), SiteViewForm.ID);
    }
    
    public void performExpand() {
    }
    
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
    }
}
