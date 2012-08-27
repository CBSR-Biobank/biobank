package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MenuItem;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.info.SiteContainerTypeInfo;
import edu.ualberta.med.biobank.common.formatters.NumberFormatter;
import edu.ualberta.med.biobank.common.permission.containerType.ContainerTypeDeletePermission;
import edu.ualberta.med.biobank.common.permission.containerType.ContainerTypeReadPermission;
import edu.ualberta.med.biobank.common.permission.containerType.ContainerTypeUpdatePermission;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.AbstractInfoTableWidget;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDoubleClickItemListener;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.HasName;
import edu.ualberta.med.biobank.model.HasNameShort;
import edu.ualberta.med.biobank.treeview.admin.ContainerTypeAdapter;
import edu.ualberta.med.biobank.treeview.admin.SiteAdapter;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ContainerTypeInfoTable extends
    InfoTableWidget<SiteContainerTypeInfo> {
    public static final I18n i18n = I18nFactory
        .getI18n(ContainerTypeInfoTable.class);

    private static class TableRowData {
        SiteContainerTypeInfo containerType;
        String name;
        String nameShort;
        Integer capacity;
        String status;
        Long inUseCount;
        Double temperature;

        @SuppressWarnings("nls")
        @Override
        public String toString() {
            return StringUtils.join(new String[] {
                name,
                nameShort,
                (capacity != null) ? capacity.toString()
                    : StringUtil.EMPTY_STRING,
                status,
                (inUseCount != null) ? inUseCount.toString()
                    : StringUtil.EMPTY_STRING,
                (temperature != null) ? temperature.toString()
                    : StringUtil.EMPTY_STRING }, "\t");
        }
    }

    @SuppressWarnings("nls")
    private static final String[] HEADINGS = new String[] {
        HasName.PropertyName.NAME.toString(),
        HasNameShort.PropertyName.NAME_SHORT.toString(),
        Capacity.NAME.singular().toString(),
        i18n.tr("Status"),
        i18n.tr("In Use"),
        Container.PropertyName.TEMPERATURE.toString() };

    private final SiteAdapter siteAdapter;

    public ContainerTypeInfoTable(Composite parent, SiteAdapter site,
        List<SiteContainerTypeInfo> containerTypeInfo) {
        super(parent, containerTypeInfo, HEADINGS, 10,
            ContainerTypeWrapper.class);
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
                        return AbstractInfoTableWidget.LOADING;
                    }
                    return StringUtil.EMPTY_STRING;
                }
                switch (columnIndex) {
                case 0:
                    return item.name;
                case 1:
                    return item.nameShort;
                case 2:
                    return NumberFormatter.format(item.capacity);
                case 3:
                    return item.status;
                case 4:
                    return NumberFormatter.format(item.inUseCount);
                case 5:
                    return NumberFormatter.format(item.temperature);
                default:
                    return StringUtil.EMPTY_STRING;
                }
            }
        };
    }

    @Override
    public Object getCollectionModelObject(Object type) throws Exception {
        TableRowData info = new TableRowData();

        SiteContainerTypeInfo containerTypeInfo = (SiteContainerTypeInfo) type;

        info.containerType =
            containerTypeInfo;
        Integer rowCapacity =
            info.containerType.getContainerType().getRowCapacity();
        Integer colCapacity =
            info.containerType.getContainerType().getColCapacity();

        info.name = info.containerType.getContainerType().getName();
        info.nameShort = info.containerType.getContainerType().getNameShort();
        info.status =
            info.containerType.getContainerType().getActivityStatus().getName();
        if ((rowCapacity != null) && (colCapacity != null)) {
            info.capacity = rowCapacity * colCapacity;
        }
        info.inUseCount = containerTypeInfo.getContainerCount();
        info.temperature =
            info.containerType.getContainerType().getDefaultTemperature();
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }

    @Override
    public SiteContainerTypeInfo getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        return ((TableRowData) item.o).containerType;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }

    @SuppressWarnings("nls")
    @Override
    public void addClickListener(
        IInfoTableDoubleClickItemListener<SiteContainerTypeInfo> listener) {
        doubleClickListeners.add(listener);
        // TODO: this code makes no sense. See jon for why.
        MenuItem mi = new MenuItem(getMenu(), SWT.PUSH);
        mi.setText(i18n.tr("Edit"));
        mi.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                SiteContainerTypeInfo selection = ContainerTypeInfoTable.this
                    .getSelection();
                if (selection != null) {
                    new ContainerTypeAdapter(siteAdapter
                        .getContainerTypesGroupNode(),
                        new ContainerTypeWrapper(
                            SessionManager.getAppService(),
                            selection.getContainerType())).openEntryForm();
                }
            }
        });
    }

    @Override
    protected Boolean canEdit(SiteContainerTypeInfo target)
        throws ApplicationException {
        return SessionManager.getAppService()
            .isAllowed(
                new ContainerTypeUpdatePermission(target.getContainerType()
                    .getId()));
    }

    @Override
    protected Boolean canDelete(SiteContainerTypeInfo target)
        throws ApplicationException {
        return SessionManager.getAppService()
            .isAllowed(
                new ContainerTypeDeletePermission(target.getContainerType()
                    .getId()));
    }

    @Override
    protected Boolean canView(SiteContainerTypeInfo target)
        throws ApplicationException {
        return SessionManager.getAppService()
            .isAllowed(
                new ContainerTypeReadPermission(target.getContainerType()));
    }
}
