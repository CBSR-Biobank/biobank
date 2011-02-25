package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.StudySourceVesselWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

@Deprecated
/**
 * this need to be rename ? to study source specimen ??
 */
public class StudySourceVesselInfoTable extends
    InfoTableWidget<StudySourceVesselWrapper> {

    private static final int PAGE_SIZE_ROWS = 5;

    protected class TableRowData {
        StudySourceVesselWrapper studySourceVessel;
        public String name;
        public String needTimeDrawn;
        public String needOriginalVolume;

        @Override
        public String toString() {
            return StringUtils.join(new String[] {
                name,
                (needTimeDrawn != null) ? needTimeDrawn.toString() : "",
                (needOriginalVolume != null) ? needOriginalVolume.toString()
                    : "" }, "\t");
        }
    }

    private final static String[] HEADINGS = new String[] { "Name",
        "Need Time Drawn", "Need Original Volume" };

    public StudySourceVesselInfoTable(Composite parent,
        List<StudySourceVesselWrapper> collection) {
        super(parent, collection, HEADINGS, PAGE_SIZE_ROWS);
    }

    @Override
    protected BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData info = (TableRowData) ((BiobankCollectionModel) element).o;
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
                    return info.needTimeDrawn;
                case 2:
                    return info.needOriginalVolume;
                default:
                    return "";
                }
            }
        };
    }

    @Override
    public TableRowData getCollectionModelObject(
        StudySourceVesselWrapper studySourceVessel) throws Exception {
        TableRowData info = new TableRowData();
        info.studySourceVessel = studySourceVessel;
        Assert.isNotNull(studySourceVessel.getSourceVessel(),
            "study source vessel has null for source vessel");
        info.name = studySourceVessel.getSourceVessel().getName();
        info.needTimeDrawn = (studySourceVessel.getNeedTimeDrawn() != null) ? (studySourceVessel
            .getNeedTimeDrawn() ? "Yes" : "No") : "No";
        info.needOriginalVolume = (studySourceVessel.getNeedOriginalVolume() != null) ? (studySourceVessel
            .getNeedOriginalVolume() ? "Yes" : "No") : "No";
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }

    @Override
    public StudySourceVesselWrapper getSelection() {
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
