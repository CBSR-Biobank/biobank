package edu.ualberta.med.biobank.model;

import edu.ualberta.med.biobank.treeview.admin.SessionAdapter;
import edu.ualberta.med.biobank.treeview.admin.SiteAdapter;

public interface ISessionNodeListener {
	public void sessionChanged(SessionAdapter sessionNode, SiteAdapter siteNode);
}
