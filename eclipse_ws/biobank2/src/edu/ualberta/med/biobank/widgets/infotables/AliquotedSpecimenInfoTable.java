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
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class AliquotedSpecimenInfoTable extends
    InfoTableWidget<AliquotedSpecimenWrapper> {

    private static final int PAGE_SIZE_ROWS = 5;

    protected static class TableRowData {
        public AliquotedSpecimenWrapper sampleStorage;
        public String typeName;
        public BigDecimal volume;
        public Integer quantity;
        public String status;

        @SuppressWarnings("nls")
        @Override
        public String toString() {
            return StringUtils.join(new String[] { typeName,
                (volume != null) ? volume.toString() : "",
                (quantity != null) ? quantity.toString() : "", status }, "\t");
        }
    }

    private static final String[] HEADINGS = new String[] {
        SpecimenType.NAME.singular().toString(),
        AliquotedSpecimen.PropertyName.VOLUME.toString(),
        AliquotedSpecimen.PropertyName.QUANTITY.toString(),
        ActivityStatus.NAME.singular().toString() };

    public AliquotedSpecimenInfoTable(Composite parent,
        List<AliquotedSpecimenWrapper> sampleStorageCollection) {
        super(parent, sampleStorageCollection, HEADINGS, PAGE_SIZE_ROWS,
            AliquotedSpecimenWrapper.class);
    }

    @SuppressWarnings("nls")
    @Override
    public TableRowData getCollectionModelObject(Object obj) throws Exception {
        TableRowData info = new TableRowData();
        info.sampleStorage = (AliquotedSpecimenWrapper) obj;
        SpecimenTypeWrapper type = info.sampleStorage.getSpecimenType();
        Assert.isNotNull(type, "sample storage - sample type is null");
        info.typeName = type.getName();
        info.volume = info.sampleStorage.getVolume();
        info.quantity = info.sampleStorage.getQuantity();
        ActivityStatus status = info.sampleStorage.getActivityStatus();
        Assert.isNotNull(status, "sample storage - activity status is null");
        info.status = status.getName();
        return info;
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
                    return item.typeName;
                case 1:
                    return NumberFormatter.format(item.volume);
                case 2:
                    return NumberFormatter.format(item.quantity);
                case 3:
                    return item.status;
                default:
                    return "";
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

    @Override
    protected Boolean canEdit(AliquotedSpecimenWrapper target)
        throws ApplicationException {
        return true;
    }

    @Override
    protected Boolean canDelete(AliquotedSpecimenWrapper target)
        throws ApplicationException {
        return true;
    }

    @Override
    protected Boolean canView(AliquotedSpecimenWrapper target)
        throws ApplicationException {
        return true;
    }
}
