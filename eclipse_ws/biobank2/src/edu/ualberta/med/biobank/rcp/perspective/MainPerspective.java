package edu.ualberta.med.biobank.rcp.perspective;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class MainPerspective implements IPerspectiveFactory {

    public static final String ID = "edu.ualberta.med.biobank.perspective.main"; //$NON-NLS-1$

    @Override
    public void createInitialLayout(IPageLayout layout) {
        layout.setEditorAreaVisible(true);
    }
}
