package edu.ualberta.med.biobank.rcp;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class ShipmentAdministrationPerspective implements IPerspectiveFactory {

    public static final String ID = "edu.ualberta.med.biobank.perspective.shipmentAdministration";

    @Override
    public void createInitialLayout(IPageLayout layout) {
        System.out.println("creation");
    }

}
