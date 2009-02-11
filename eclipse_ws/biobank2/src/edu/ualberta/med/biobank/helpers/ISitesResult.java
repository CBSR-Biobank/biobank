package edu.ualberta.med.biobank.helpers;

import java.util.List;

import edu.ualberta.med.biobank.model.Site;

public interface ISitesResult {
	public void callback(List<Site> sites);
}
