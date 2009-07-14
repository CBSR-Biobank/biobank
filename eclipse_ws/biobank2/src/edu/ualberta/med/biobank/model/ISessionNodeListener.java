package edu.ualberta.med.biobank.model;

import edu.ualberta.med.biobank.treeview.SessionAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;

public interface ISessionNodeListener {
	public void sessionChanged(SessionAdapter sessionNode, SiteAdapter siteNode);
}
