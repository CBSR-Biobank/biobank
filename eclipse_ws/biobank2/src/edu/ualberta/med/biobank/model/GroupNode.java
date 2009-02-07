package edu.ualberta.med.biobank.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;


public class GroupNode<S> extends WsObject {

	private List<S> items;
	
	public GroupNode(WsObject parent, String name) {
		super(parent);
		setName(name);
		items = new ArrayList<S>();
	}
	
	public List<S> getItems() {
		return items;
	}
	
	public S getItem(int i) {
		Assert.isTrue(i < items.size(), "Invalid index: " + i);
		return items.get(i);
	}
	
	public void addChild(S child) {	
		int id = 0;
		String name;
		
		if (child.getClass().equals(ClinicNode.class)) {
			ClinicNode clinicNode  = (ClinicNode) child;
			id = clinicNode.getId();
			name = clinicNode.getName();
		}
		
		if (containsItem(id)) {
			// don't add - assume our model is up to date 
			return;
		}

		S item = getNamedItem(name);
		if (item != null) {
			// may have inserted a new site into database
			clinicNode.setClinic(clinic);
			return;
		}
			
		items.add(child);
	}
	
	public void removeClinic(Clinic clinic) {		
		ClinicNode nodeToRemove = null;

		for (ClinicNode node : clinicNodes) {
			if (node.getClinic().getId().equals(clinic.getId()) 
					|| node.getClinic().getName().equals(clinic.getName()))
				nodeToRemove = node;
		}
		
		if (nodeToRemove != null)
			clinicNodes.remove(nodeToRemove);
	}

	public void removeByName(String name) {	
		if (items.size() == 0) return null;
		
		S itemToRemove = null;
		
		S item = items.get(0);
		if (item.getClass().equals(ClinicNode.class)) {

		for (ClinicNode node : clinicNodes) {
			if (node.getClinic().getName().equals(name))
				nodeToRemove = node;
		}
		
		if (itemToRemove != null)
			items.remove(itemToRemove);
	}
	
	public boolean containsItem(int id) {		
		for (S node : items) {
			if (node.getClass().equals(ClinicNode.class)) {
				ClinicNode clinicNode  = (ClinicNode) node;
				if (clinicNode.getClinic().getId().equals(id)) return true;				
			}
		}
		return false;
	}
	
	public S getNamedItem(String name) {
		if (items.size() == 0) return null;
		
		S item = items.get(0);
		if (item.getClass().equals(ClinicNode.class)) {
			for (S node : items) {
				ClinicNode clinicNode  = (ClinicNode) node;
				if (clinicNode.getName().equals(name)) return item;	
			}	
		}
		return null;
	}
	
	@Override
	public int getId() {
		return 0;
	}
}
