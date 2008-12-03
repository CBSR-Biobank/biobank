package edu.ualberta.med.biobank.model;

import gov.nih.nci.system.applicationservice.ApplicationService;

import java.util.ArrayList;
import org.eclipse.core.runtime.ListenerList;

public class SessionNode extends WsObject {
	private ArrayList<BioBankNode> bioBankNodes;
	
	private ListenerList listeners;
	
	private ApplicationService appService;
	
	public SessionNode(ApplicationService appService, String name) {
		this.appService = appService;
		setName(name);
	}
	
	public void addBioBank(BioBank bioBank) {
		if (bioBankNodes == null) {
			bioBankNodes = new ArrayList<BioBankNode>();
		}
		BioBankNode bioBankNode = new BioBankNode(bioBank);
		bioBankNode.setParent(this);
		bioBankNodes.add(bioBankNode);
		fireChildrenChanged(null);
	}

	public void removeBioBank(BioBank bioBank) {
		if (bioBankNodes != null) {
			bioBankNodes.remove(bioBank);
			if (bioBankNodes.isEmpty())
				bioBankNodes = null;
		}
	}	
	
	public BioBankNode[] getBioBanks() {
		if (bioBankNodes == null) {
			return new BioBankNode[0];
		}
		return (BioBankNode[]) bioBankNodes.toArray(new BioBankNode[bioBankNodes.size()]);
	}


	public void addListener(ISessionNodeListener listener) {
		if (listeners == null)
			listeners = new ListenerList();
		listeners.add(listener);
	}

	public void removeListener(ISessionNodeListener listener) {
		if (listeners != null) {
			listeners.remove(listener);
			if (listeners.isEmpty())
				listeners = null;
		}
	}

	protected void fireChildrenChanged(BioBankNode bioBankNode) {
		if (listeners == null) return;
		
		for (Object l : listeners.getListeners()) {
			((ISessionNodeListener) l).sessionChanged(this, bioBankNode);
		}
	}
}
