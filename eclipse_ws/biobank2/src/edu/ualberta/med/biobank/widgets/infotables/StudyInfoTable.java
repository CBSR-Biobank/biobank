package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.formatters.NumberFormatter;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;

public class StudyInfoTable extends InfoTableWidget<StudyWrapper> {

    protected static class TableRowData {
        StudyWrapper study;
        String name;
        String nameShort;
        String status;
        Long patientCount;
        Long visitCount;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { name, nameShort, status,
                (patientCount != null) ? patientCount.toString() : "", 
                (visitCount != null) ? visitCount.toString() : "" }, "\t");  
        }
    }

    private static final String[] HEADINGS = new String[] {
        "",
        "",
        "",
        "",
        "" };

    public StudyInfoTable(Composite parent, List<StudyWrapper> collection) {
        super(parent, collection, HEADINGS, 10, StudyWrapper.class);
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
                    return info.nameShort;
                case 2:
                    return (info.status != null) ? info.status : ""; 
                case 3:
                    return NumberFormatter.format(info.patientCount);
                case 4:
                    return NumberFormatter.format(info.visitCount);
                default:
                    return ""; 
                }
            }
        };
    }

    @Override
    public Object getCollectionModelObject(Object study) throws Exception {
        TableRowData info = new TableRowData();
        info.study = (StudyWrapper) study;
        info.name = info.study.getName();
        info.nameShort = info.study.getNameShort();
        info.status = info.study.getActivityStatus().getName();
        if (info.status == null) {
            info.status = ""; 
        }
        info.patientCount = info.study.getPatientCount(true);
        info.visitCount = info.study.getCollectionEventCount();
        info.study.reload();
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }

    @Override
    public StudyWrapper getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        TableRowData row = (TableRowData) item.o;
        Assert.isNotNull(row);
        return row.study;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }

}
