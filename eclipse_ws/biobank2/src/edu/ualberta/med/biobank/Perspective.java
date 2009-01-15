package edu.ualberta.med.biobank;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import edu.ualberta.med.biobank.session.SessionsView;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		//layout.setEditorAreaVisible(false); 
		//layout.addStandaloneView(SessionsView.ID, true /* show title */,
		//		IPageLayout.LEFT, 1.0f, layout.getEditorArea());
	}
}
