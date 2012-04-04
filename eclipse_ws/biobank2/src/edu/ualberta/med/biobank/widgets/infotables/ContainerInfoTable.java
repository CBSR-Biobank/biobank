package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MenuItem;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.formatters.NumberFormatter;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDoubleClickItemListener;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.treeview.admin.ContainerAdapter;
import edu.ualberta.med.biobank.treeview.admin.SiteAdapter;

public class ContainerInfoTable extends InfoTableWidget<Container> {

    private static class TableRowData {
        Container container;
        String label;
        String typeNameShort;
        String status;
        String barcode;
        Double temperature;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { label, typeNameShort,
                status, barcode,
                (temperature != null) ? temperature.toString() : "" }, "\t");  
        }
    }

    private static final String[] HEADINGS = new String[] {
        "Name",
        "Container Type",
        "Status",
        "Product Barcode",
        "Temperature" };

    private SiteAdapter siteAdapter;

    public ContainerInfoTable(Composite parent, SiteAdapter site,
        List<Container> containers) {
        super(parent, containers, HEADINGS, 10, ContainerWrapper.class);
        siteAdapter = site;
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
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
                    return item.label;
                case 1:
                    return item.typeNameShort;
                case 2:
                    return item.status;
                case 3:
                    return item.barcode;
                case 4:
                    NumberFormatter.format(item.temperature);
                default:
                    return ""; 
                }
            }
        };
    }

    @Override
    public Object getCollectionModelObject(Object obj) throws Exception {
        TableRowData info = new TableRowData();

        Container container = (Container) obj;

        info.container =
            container;
        info.label = info.container.getLabel();
        ContainerType type = info.container.getContainerType();
        if (type != null) {
            info.typeNameShort = type.getNameShort();
        }
        info.status = info.container.getActivityStatus().getName();
        info.barcode = info.container.getProductBarcode();
        info.temperature = info.container.getTopContainer().getTemperature();
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }

    @Override
    public Container getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        return ((TableRowData) item.o).container;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }

    @Override
    public void addClickListener(
        IInfoTableDoubleClickItemListener<Container> listener) {
        doubleClickListeners.add(listener);
        MenuItem mi = new MenuItem(getMenu(), SWT.PUSH);
        mi.setText("Edit");
        mi.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Container selection = ContainerInfoTable.this
                    .getSelection();
                if (selection != null) {
                    new ContainerAdapter(siteAdapter.getContainersGroupNode(),
                        new ContainerWrapper(SessionManager.getAppService(),
                            selection)).openEntryForm();
                }
            }
        });
    }

}
