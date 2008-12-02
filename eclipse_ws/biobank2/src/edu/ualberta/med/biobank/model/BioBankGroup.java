package edu.ualberta.med.biobank.model;

import java.util.ArrayList;
import org.eclipse.core.runtime.ListenerList;

public class BioBankGroup extends WsObject {
	private ArrayList<BioBank> bioBanks;
	
	private ListenerList listeners;
	
	public void addBioBank(BioBank bioBank) {
		if (bioBanks == null) {
			bioBanks = new ArrayList<BioBank>();
		}
		bioBanks.add(bioBank);
		fireChildrenChanged(null);
	}

	public void removeBioBank(BioBank bioBank) {
		if (bioBanks != null) {
			bioBanks.remove(bioBank);
			if (bioBanks.isEmpty())
				bioBanks = null;
		}
	}
		
	
	public BioBank[] getBioBanks() {
		if (bioBanks == null) {
			return new BioBank[0];
		}
		return (BioBank[]) bioBanks.toArray(new BioBank[bioBanks.size()]);
	}


	public void addListener(IBioBankGroupListener listener) {
		if (listeners == null)
			listeners = new ListenerList();
		listeners.add(listener);
	}

	public void removeListener(IBioBankGroupListener listener) {
		if (listeners != null) {
			listeners.remove(listener);
			if (listeners.isEmpty())
				listeners = null;
		}
	}

	protected void fireChildrenChanged(BioBank bioBank) {
		if (listeners == null) return;
		
		for (Object l : listeners.getListeners()) {
			((IBioBankGroupListener) l).bioBankChanged(this, bioBank);
		}
	}
}
