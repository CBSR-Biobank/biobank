package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.StudySourceVesselWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class StudySourceVesselInfoTable extends
    InfoTableWidget<StudySourceVesselWrapper> {

    private static final int PAGE_SIZE_ROWS = 5;

    protected class TableRowData {
        StudySourceVesselWrapper studySourceVessel;
        String name;
        Boolean needTimeDrawn;
        Boolean needReceivedVolume;

        @Override
        public String toString() {
            return StringUtils.join(new String[] {
                name,
                (needTimeDrawn != null) ? needTimeDrawn.toString() : "",
                (needReceivedVolume != null) ? needReceivedVolume.toString()
                    : "" }, "\t");
        }
    }

    @SuppressWarnings("unused")
    private class TableSorter extends BiobankTableSorter {
        @Override
        public int compare(Viewer viewer, Object e1, Object e2) {
            TableRowData i1 = (TableRowData) ((BiobankCollectionModel) e1).o;
            TableRowData i2 = (TableRowData) ((BiobankCollectionModel) e2).o;
            if (i1 == null) {
                return -1;
            } else if (i2 == null) {
                return 1;
            }
            int rc = 0;
            switch (propertyIndex) {
            case 0:
                rc = compare(i1.name, i2.name);
                break;
            case 1:
                rc = compare(i1.needTimeDrawn, i2.needTimeDrawn);
                break;
            case 2:
                rc = compare(i1.needReceivedVolume, i2.needReceivedVolume);
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

    private final static String[] HEADINGS = new String[] { "Name",
        "Need Time Drawn", "Need Received Volume" };

    private final static int[] BOUNDS = new int[] { 250, 150, -1, -1, -1 };

    public StudySourceVesselInfoTable(Composite parent,
        List<StudySourceVesselWrapper> collection) {
        super(parent, collection, HEADINGS, BOUNDS, PAGE_SIZE_ROWS);
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
                    return (info.needTimeDrawn != null) ? info.needTimeDrawn
                        .toString() : Boolean.FALSE.toString();
                case 2:
                    return (info.needReceivedVolume != null) ? info.needReceivedVolume
                        .toString()
                        : Boolean.FALSE.toString();
                default:
                    return "";
                }
            }
        };
    }

    @Override
    protected BiobankTableSorter getTableSorter() {
        // return new TableSorter();
        return null;
    }

    @Override
    public Object getCollectionModelObject(
        StudySourceVesselWrapper studySourceVessel) throws Exception {
        TableRowData info = new TableRowData();
        info.studySourceVessel = studySourceVessel;
        Assert.isNotNull(studySourceVessel.getSourceVessel(),
            "study source vessel has null for source vessel");
        info.name = studySourceVessel.getSourceVessel().getName();
        info.needTimeDrawn = studySourceVessel.getNeedTimeDrawn();
        info.needReceivedVolume = studySourceVessel.getNeedReceivedVolume();
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }

    @Override
    public List<StudySourceVesselWrapper> getCollection() {
        List<StudySourceVesselWrapper> result = new ArrayList<StudySourceVesselWrapper>();
        for (BiobankCollectionModel item : model) {
            result.add(((TableRowData) item.o).studySourceVessel);
        }
        return result;
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

}
