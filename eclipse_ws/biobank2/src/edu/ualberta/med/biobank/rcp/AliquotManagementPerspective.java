package edu.ualberta.med.biobank.rcp;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * Perspective to manage samples using scanners
 */
public class AliquotManagementPerspective implements IPerspectiveFactory {

    public final static String ID = "edu.ualberta.med.biobank.perspective.samplesManagement";

    @Override
    public void createInitialLayout(IPageLayout layout) {
        layout.setEditorAreaVisible(false);
    }
}
