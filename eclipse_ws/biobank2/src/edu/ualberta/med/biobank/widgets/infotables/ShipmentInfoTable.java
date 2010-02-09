package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingCompanyWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class ShipmentInfoTable extends InfoTableWidget<ShipmentWrapper> {

    class TableSorter extends ViewerSorter {

        private int propertyIndex = 0;

        private int direction = 0;

        public void setColumn(int colId) {
            if (propertyIndex == colId) {
                direction = 1 - direction;
            } else {
                propertyIndex = colId;
                direction = 1 - direction;
            }
        }

        @Override
        public int compare(Viewer viewer, Object e1, Object e2) {
            ShipmentWrapper s1 = (ShipmentWrapper) ((BiobankCollectionModel) e1).o;
            ShipmentWrapper s2 = (ShipmentWrapper) ((BiobankCollectionModel) e2).o;
            if ((s1 == null) || (s2 == null)) {
                return -1;
            }
            int rc = 0;
            switch (propertyIndex) {
            case 0:
                rc = s1.getDateReceived().compareTo(s2.getDateReceived());
                break;
            case 1:
                rc = s1.getWaybill().compareTo(s2.getWaybill());
                break;
            case 2:
                ShippingCompanyWrapper sc1 = s1.getShippingCompany();
                ShippingCompanyWrapper sc2 = s2.getShippingCompany();
                if ((sc1 == null) || (sc2 == null)) {
                    return -1;
                }
                rc = sc1.compareTo(sc2);
                break;
            case 3:
                int sz1 = s1.getPatientCollection().size();
                int sz2 = s2.getPatientCollection().size();
                rc = (sz1 == sz2) ? 0 : (sz1 < sz2) ? -1 : 1;
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

    private TableSorter tableSorter;

    public ShipmentInfoTable(Composite parent, ClinicWrapper clinic) {
        super(parent, clinic.getShipmentCollection(), HEADINGS, BOUNDS);
        tableSorter = new TableSorter();
        tableViewer.setSorter(tableSorter);

        final Table table = tableViewer.getTable();
        int count = 0;
        for (final TableViewerColumn col : tableViewColumns) {
            final int index = count;
            col.getColumn().addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    tableSorter.setColumn(index);
                    int dir = table.getSortDirection();
                    if (table.getSortColumn() == col.getColumn()) {
                        dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
                    } else {
                        dir = SWT.DOWN;
                    }
                    table.setSortDirection(dir);
                    table.setSortColumn(col.getColumn());
                    tableViewer.refresh();
                }
            });
            ++count;
        }
    }

    @Override
    public BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                ShipmentWrapper ship = (ShipmentWrapper) ((BiobankCollectionModel) element).o;
                if (ship == null)
                    return null;
                switch (columnIndex) {
                case 0:
                    return ship.getFormattedDateReceived();
                case 1:
                    return ship.getWaybill();
                case 2:
                    ShippingCompanyWrapper company = ship.getShippingCompany();
                    if (company != null) {
                        return company.getName();
                    }
                    return "";
                case 3:
                    List<PatientWrapper> patients = ship.getPatientCollection();
                    if (patients == null) {
                        return "0";
                    }
                    return new Integer(patients.size()).toString();
                default:
                    return "";
                }
            }
        };
    }
}
