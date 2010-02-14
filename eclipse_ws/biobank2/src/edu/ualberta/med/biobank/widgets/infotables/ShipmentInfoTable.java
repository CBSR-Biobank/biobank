package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingCompanyWrapper;
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

    private class TableSorter extends BiobankTableSorter {
        @Override
        public int compare(Viewer viewer, Object e1, Object e2) {
            TableRowData s1 = (TableRowData) ((BiobankCollectionModel) e1).o;
            TableRowData s2 = (TableRowData) ((BiobankCollectionModel) e2).o;
            if ((s1 == null) || (s2 == null)) {
                return -1;
            }
            int rc = 0;
            switch (propertyIndex) {
            case 0:
                rc = s1.dateReceived.compareTo(s2.dateReceived);
                break;
            case 1:
                rc = s1.waybill.compareTo(s2.waybill);
                break;
            case 2:
                rc = s1.shippingCompany.compareTo(s2.shippingCompany);
                break;
            case 3:
                rc = s1.numPatients.compareTo(s2.numPatients);
                break;
            default:
                rc = 0;
            }
            // If descending order, flip the direction
            if (direction == 1) {
                rc = -rc;
            }
            return rc;
        }
    }

    private static final String[] HEADINGS = new String[] { "Date received",
        "Waybill", "Shipping company", "No. Patients" };

    private static final int[] BOUNDS = new int[] { 180, 140, 140, 100, -1 };

    public ShipmentInfoTable(Composite parent, ClinicWrapper clinic) {
        super(parent, true, clinic.getShipmentCollection(), HEADINGS, BOUNDS);
        setSorter(new TableSorter());
    }

    @Override
    public BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData item = (TableRowData) ((BiobankCollectionModel) element).o;
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
        info.dateReceived = shipment.getFormattedDateReceived();
        info.waybill = shipment.getWaybill();
        ShippingCompanyWrapper company = shipment.getShippingCompany();
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
    public List<ShipmentWrapper> getCollection() {
        List<ShipmentWrapper> result = new ArrayList<ShipmentWrapper>();
        for (BiobankCollectionModel item : model) {
            result.add(((TableRowData) item.o).shipment);
        }
        return result;
    }

    @Override
    public ShipmentWrapper getSelection() {
        TableRowData item = (TableRowData) getSelectionInternal().o;
        Assert.isNotNull(item);
        return item.shipment;
    }
}
