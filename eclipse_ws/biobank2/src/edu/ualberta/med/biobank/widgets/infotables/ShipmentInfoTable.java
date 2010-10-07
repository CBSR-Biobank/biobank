package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class ShipmentInfoTable extends InfoTableWidget<ShipmentWrapper> {

    private class TableRowData {
        ShipmentWrapper shipment;
        String dateReceived;
        String waybill;
        String shippingCompany;
        Integer numPatients;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { dateReceived, waybill,
                shippingCompany, numPatients.toString() }, "\t");
        }
    }

    private static final String[] HEADINGS = new String[] { "Date received",
        "Waybill", "Shipping company", "No. Patients" };

    private static final int[] BOUNDS = new int[] { 180, 140, 140, 100, -1 };

    public ShipmentInfoTable(Composite parent, ClinicWrapper clinic) {
        super(parent, clinic.getShipmentCollection(), HEADINGS, BOUNDS, 10);
    }

    @Override
    protected BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData item =
                    (TableRowData) ((BiobankCollectionModel) element).o;
                if (item == null) {
                    if (columnIndex == 0) {
                        return "loading...";
                    }
                    return "";
                }
                switch (columnIndex) {
                case 0:
                    return item.dateReceived;
                case 1:
                    return item.waybill;
                case 2:
                    return item.shippingCompany;
                case 3:
                    return item.numPatients.toString();
                default:
                    return "";
                }
            }
        };
    }

    /**
     * Required since the shipping company object must be loaded for every
     * shipment.
     */
    @Override
    public Object getCollectionModelObject(ShipmentWrapper shipment)
        throws Exception {
        TableRowData info = new TableRowData();
        info.shipment = shipment;
        info.dateReceived = shipment.getFormattedDateReceived();
        info.waybill = shipment.getWaybill();
        ShippingMethodWrapper company = shipment.getShippingMethod();
        if (company != null) {
            info.shippingCompany = company.getName();
        } else {
            info.shippingCompany = new String();
        }
        List<PatientWrapper> patients = shipment.getPatientCollection();
        if (patients == null) {
            info.numPatients = 0;
        } else {
            info.numPatients = patients.size();
        }
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }

    @Override
    public ShipmentWrapper getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        TableRowData row = (TableRowData) item.o;
        Assert.isNotNull(row);
        return row.shipment;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }
}
