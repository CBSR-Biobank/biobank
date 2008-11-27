package edu.ualberta.med.biobank.model;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.ListenerList;

public class StudyGroup {
	private ArrayList<Study> studies;
	
	private ListenerList listeners;
	
	public StudyGroup() {	
	}
	
	public void addStudy(Study study) {
		if (studies == null) {
			studies = new ArrayList();
		}
		studies.add(study);
		fireStudiesChanged(null);
	}

	public void remove(Study study) {
		if (studies != null) {
			studies.remove(study);
			if (studies.isEmpty())
				studies = null;
		}
		fireStudiesChanged(null);
	}
	
	public Study[] getStudies() {
		if (studies == null) {
			return new Study[0];
		}
		return (Study[]) studies.toArray();
	}


	public void addStudiesListener(IStudiesListener listener) {
		if (listeners == null)
			listeners = new ListenerList();
		listeners.add(listener);
	}

	public void removeStudiesListener(IStudiesListener listener) {
		if (listeners != null) {
			listeners.remove(listener);
			if (listeners.isEmpty())
				listeners = null;
		}
	}

	protected void fireStudiesChanged(Study study) {
		if (listeners == null) return;
		
		for (Object l : listeners.getListeners()) {
			((IStudiesListener) l).studiesChanged(this, study);
		}
	}
}
