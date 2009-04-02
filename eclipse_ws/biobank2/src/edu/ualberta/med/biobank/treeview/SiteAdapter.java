package edu.ualberta.med.biobank.treeview;

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
}
