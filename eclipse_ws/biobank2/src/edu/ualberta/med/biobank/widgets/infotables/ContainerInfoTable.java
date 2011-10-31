package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MenuItem;

import edu.ualberta.med.biobank.common.formatters.NumberFormatter;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.admin.SiteAdapter;
import edu.ualberta.med.biobank.treeview.util.AdapterFactory;

public class ContainerInfoTable extends InfoTableWidget<Container> {

    private static class TableRowData {
        ContainerWrapper container;
        String label;
        String typeNameShort;
        String status;
        String barcode;
        Double temperature;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { label, typeNameShort,
                status, barcode,
                (temperature != null) ? temperature.toString() : "" }, "\t"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    private static final String[] HEADINGS = new String[] {
        Messages.ContainerInfoTable_name_label,
        Messages.ContainerInfoTable_type_label,
        Messages.ContainerInfoTable_status_label,
        Messages.ContainerInfoTable_barcode_label,
        Messages.ContainerInfoTable_temperature_label };

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
                TableRowData item = (TableRowData) ((BiobankCollectionModel) element).o;
                if (item == null) {
                    if (columnIndex == 0) {
                        return Messages.ContainerInfoTable_loading;
                    }
                    return ""; //$NON-NLS-1$
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
                    return ""; //$NON-NLS-1$
                }
            }
        };
    }

    @Override
    public Object getCollectionModelObject(Object obj) throws Exception {
        TableRowData info = new TableRowData();

        Container container = (Container) obj;

        info.container = new ContainerWrapper(siteAdapter.getAppService(),
            container);
        info.label = info.container.getLabel();
        ContainerTypeWrapper type = info.container.getContainerType();
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
    public ContainerWrapper getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        TableRowData row = (TableRowData) item.o;
        Assert.isNotNull(row);
        return row.container;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }

    @Override
    public void addClickListener(IDoubleClickListener listener) {
        doubleClickListeners.add(listener);
        MenuItem mi = new MenuItem(getMenu(), SWT.PUSH);
        mi.setText(Messages.ContainerInfoTable_edit_label);
        mi.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                ModelWrapper<?> selection = ContainerInfoTable.this
                    .getSelection();
                if (selection != null) {
                    AdapterBase adapter = AdapterFactory.getAdapter(selection);
                    adapter.setParent(siteAdapter.getContainersGroupNode());
                    adapter.openEntryForm();
                }
            }
        });
    }

}
