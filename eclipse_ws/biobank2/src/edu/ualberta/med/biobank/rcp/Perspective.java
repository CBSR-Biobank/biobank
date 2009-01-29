package edu.ualberta.med.biobank.rcp;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import edu.ualberta.med.biobank.session.SessionsView;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		layout.addStandaloneView(SessionsView.ID, true, IPageLayout.LEFT, 0.3f, layout.getEditorArea());
		layout.getViewLayout(SessionsView.ID).setCloseable(false);
		layout.setEditorAreaVisible(true); 
	}
}
