package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.SourceSpecimenWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import gov.nih.nci.system.applicationservice.ApplicationException;

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

        @SuppressWarnings("nls")
        @Override
        public String toString() {
            return StringUtils.join(new String[] {
                name,
                (needOriginalVolume != null) ? needOriginalVolume.toString()
                    : "" }, "\t");
        }
    }

    private final static String[] HEADINGS = new String[] {
        SpecimenType.NAME.singular().toString(),
        SourceSpecimen.PropertyName.NEED_ORIGINAL_VOLUME.toString() };

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
                TableRowData info =
                    (TableRowData) ((BiobankCollectionModel) element).o;
                if (info == null) {
                    if (columnIndex == 0) {
                        return "loading...";
                    }
                    return "";
                }
                switch (columnIndex) {
                case 0:
                    return info.name;
                case 1:
                    return info.needOriginalVolume;
                default:
                    return "";
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
            "study specimen type is null");
        info.name = info.studySourceVessel.getSpecimenType().getName();
        info.needOriginalVolume =
            (info.studySourceVessel
                .getNeedOriginalVolume() != null) ? (info.studySourceVessel
                .getNeedOriginalVolume() ? "Yes"
                : "No")
                : "No";
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

    @Override
    protected Boolean canEdit(SourceSpecimenWrapper target)
        throws ApplicationException {
        return true;
    }

    @Override
    protected Boolean canDelete(SourceSpecimenWrapper target)
        throws ApplicationException {
        return true;
    }

    @Override
    protected Boolean canView(SourceSpecimenWrapper target)
        throws ApplicationException {
        return true;
    }

}
