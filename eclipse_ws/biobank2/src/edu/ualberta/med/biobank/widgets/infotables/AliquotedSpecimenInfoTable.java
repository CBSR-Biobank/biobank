package edu.ualberta.med.biobank.widgets.infotables;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.formatters.NumberFormatter;
import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.model.ActivityStatus;

public class AliquotedSpecimenInfoTable extends
    InfoTableWidget<AliquotedSpecimenWrapper> {

    private static final int PAGE_SIZE_ROWS = 5;

    protected static class TableRowData {
        public AliquotedSpecimenWrapper sampleStorage;
        public String typeName;
        public BigDecimal volume;
        public Integer quantity;
        public String status;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { typeName,
                (volume != null) ? volume.toString() : "", //$NON-NLS-1$
                (quantity != null) ? quantity.toString() : "", status }, "\t"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    private static final String[] HEADINGS = new String[] {
        Messages.AliquotedSpecimen_field_type_label,
        Messages.AliquotedSpecimen_field_volume_label,
        Messages.AliquotedSpecimen_field_quantity_label,
        Messages.label_activity };

    public AliquotedSpecimenInfoTable(Composite parent,
        List<AliquotedSpecimenWrapper> sampleStorageCollection) {
        super(parent, sampleStorageCollection, HEADINGS, PAGE_SIZE_ROWS,
            AliquotedSpecimenWrapper.class);
    }

    @Override
    public TableRowData getCollectionModelObject(Object obj) throws Exception {
        TableRowData info = new TableRowData();
        info.sampleStorage = (AliquotedSpecimenWrapper) obj;
        SpecimenTypeWrapper type = info.sampleStorage.getSpecimenType();
        Assert.isNotNull(type, "sample storage - sample type is null"); //$NON-NLS-1$
        info.typeName = type.getName();
        info.volume = info.sampleStorage.getVolume();
        info.quantity = info.sampleStorage.getQuantity();
        ActivityStatus status = info.sampleStorage.getActivityStatus();
        Assert.isNotNull(status, "sample storage - activity status is null"); //$NON-NLS-1$
        info.status = status.getName();
        return info;
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData item = (TableRowData) ((BiobankCollectionModel) element).o;
                if (item == null) {
                    if (columnIndex == 0) {
                        return Messages.infotable_loading_msg;
                    }
                    return ""; //$NON-NLS-1$
                }
                switch (columnIndex) {
                case 0:
                    return item.typeName;
                case 1:
                    return NumberFormatter.format(item.volume);
                case 2:
                    return NumberFormatter.format(item.quantity);
                case 3:
                    return item.status;
                default:
                    return ""; //$NON-NLS-1$
                }
            }
        };
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }

    @Override
    public AliquotedSpecimenWrapper getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        TableRowData row = (TableRowData) item.o;
        Assert.isNotNull(row);
        return row.sampleStorage;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }
}
