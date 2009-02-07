package edu.ualberta.med.biobank.treeview;

import java.util.Arrays;

import edu.ualberta.med.biobank.model.Site;

public class SiteAdapter extends Node {
	private Site site;

	public SiteAdapter(SessionAdapter parent, Site site) {
		super(parent);
		this.site = site;
		children = Arrays.asList(
			new Node(this, 1, "Studies", true),
			new Node(this, 2, "Clinics", true),
			new Node(this, 3, "Storage Types", true)
		);
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public Site getSite() {
		return site;
	}
	
	public Node getStudiesGroupNode() {
		return children.get(0);
	}
	
	public Node getClinicGroupNode() {
		return children.get(1);
	}

	@Override
	public int getId() {
		Object o = (Object) site.getId();
		if (o == null) return 0;
		return site.getId();
	}

	@Override
	public String getName() {
		Object o = (Object) site.getId();
		if (o == null) return null;
		return site.getName();
	}
}
