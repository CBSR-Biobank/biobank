package edu.ualberta.med.biobank.widgets.infotables;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;

public class ShipmentInfoTable extends InfoTableWidget<ShipmentWrapper> {

    private static final String[] HEADINGS = new String[] { "Date received",
        "Waybill", "Shipping company", "No. Patients" };

    private static final int[] BOUNDS = new int[] { 180, 140, 140, 100, -1 };

    public ShipmentInfoTable(Composite parent, ClinicWrapper clinic) {
        super(parent, clinic.getShipmentCollection(), HEADINGS, BOUNDS);
    }

}
