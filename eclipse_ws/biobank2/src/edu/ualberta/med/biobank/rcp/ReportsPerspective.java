package edu.ualberta.med.biobank.rcp;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class ReportsPerspective implements IPerspectiveFactory {

    public static final String ID = "edu.ualberta.med.biobank.perspective.reports";

    @Override
    public void createInitialLayout(IPageLayout layout) {
        layout.setEditorAreaVisible(false);
    }

}
