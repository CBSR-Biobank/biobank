package edu.ualberta.med.biobank.rcp;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		// layout.addView(SessionsView.ID, IPageLayout.LEFT, 0.3f, layout
		// .getEditorArea());
		// layout.getViewLayout(SessionsView.ID).setCloseable(false);
		// layout.setEditorAreaVisible(false);
		//
		// layout.addView(SearchView.ID, IPageLayout.TOP, ratio, refId)
		// IFolderLayout top = layout.createFolder("top", IPageLayout.TOP, 026f,
		// layout.getEditorArea());
		// top.addView();
	}
}
