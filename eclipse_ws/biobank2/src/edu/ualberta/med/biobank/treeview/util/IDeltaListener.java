package edu.ualberta.med.biobank.treeview.util;


public interface IDeltaListener {
	public void add(DeltaEvent event);
	public void remove(DeltaEvent event);
}
