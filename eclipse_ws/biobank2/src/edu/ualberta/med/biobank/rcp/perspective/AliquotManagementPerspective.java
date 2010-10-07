package edu.ualberta.med.biobank.rcp.perspective;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * Perspective to manage samples using scanners
 */
public class AliquotManagementPerspective implements IPerspectiveFactory {

    public final static String ID = "edu.ualberta.med.biobank.perspective.aliquotsManagement";

    @Override
    public void createInitialLayout(IPageLayout layout) {
        layout.setEditorAreaVisible(false);
    }
}
