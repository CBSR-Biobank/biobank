package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.SourceSpecimenWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;

/**
 * this need to be rename ? to study source specimen ??
 */
public class SourceSpecimenInfoTable extends
    InfoTableWidget<SourceSpecimenWrapper> {

    private static final int PAGE_SIZE_ROWS = 5;

    protected static class TableRowData {
        SourceSpecimenWrapper studySourceVessel;
        public String name;
        public String needOriginalVolume;

        @Override
        public String toString() {
            return StringUtils.join(new String[] {
                name,
                (needOriginalVolume != null) ? needOriginalVolume.toString()
                    : "" }, "\t"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    private final static String[] HEADINGS = new String[] {
        Messages.SourceSpecimen_field_type_label,
        Messages.SourceSpecimen_field_originalVolume_label };

    public SourceSpecimenInfoTable(Composite parent,
        List<SourceSpecimenWrapper> collection) {
        super(parent, collection, HEADINGS, PAGE_SIZE_ROWS,
            SourceSpecimenWrapper.class);
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData info = (TableRowData) ((BiobankCollectionModel) element).o;
                if (info == null) {
                    if (columnIndex == 0) {
                        return Messages.SourceSpecimenInfoTable_loading;
                    }
                    return ""; //$NON-NLS-1$
                }
                switch (columnIndex) {
                case 0:
                    return info.name;
                case 1:
                    return info.needOriginalVolume;
                default:
                    return ""; //$NON-NLS-1$
                }
            }
        };
    }

    @Override
    public TableRowData getCollectionModelObject(Object studySourceVessel)
        throws Exception {
        TableRowData info = new TableRowData();
        info.studySourceVessel = (SourceSpecimenWrapper) studySourceVessel;
        Assert.isNotNull(info.studySourceVessel.getSpecimenType(),
            "study specimen type is null"); //$NON-NLS-1$
        info.name = info.studySourceVessel.getSpecimenType().getName();
        info.needOriginalVolume = (info.studySourceVessel
            .getNeedOriginalVolume() != null) ? (info.studySourceVessel
            .getNeedOriginalVolume() ? Messages.SourceSpecimenInfoTable_yes_label
            : Messages.SourceSpecimenInfoTable_no_label)
            : Messages.SourceSpecimenInfoTable_no_label;
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }

    @Override
    public SourceSpecimenWrapper getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        TableRowData row = (TableRowData) item.o;
        Assert.isNotNull(row);
        return row.studySourceVessel;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }

}
