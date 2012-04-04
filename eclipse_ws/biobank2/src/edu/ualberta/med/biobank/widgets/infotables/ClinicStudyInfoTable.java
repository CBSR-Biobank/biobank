package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.action.info.StudyCountInfo;
import edu.ualberta.med.biobank.common.formatters.NumberFormatter;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;

public class ClinicStudyInfoTable extends InfoTableWidget<StudyCountInfo> {

    private static class TableRowData {
        public StudyCountInfo study;
        public String studyShortName;
        public Long patientCount;
        public Long visitCount;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { studyShortName,
                (patientCount != null) ? patientCount.toString() : "", 
                (visitCount != null) ? visitCount.toString() : "" }, "\t");  
        }
    }

    private static final String[] HEADINGS = new String[] {
        "Study",
        "#Patients",
        "#Collection Events" };

    public ClinicStudyInfoTable(Composite parent,
        List<StudyCountInfo> studyCountInfo) {
        super(parent, null, HEADINGS, 10, StudyWrapper.class);
        setList(studyCountInfo);
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
                    return item.studyShortName;
                case 1:
                    return NumberFormatter.format(item.patientCount);
                case 2:
                    return NumberFormatter.format(item.visitCount);
                default:
                    return ""; 
                }
            }
        };
    }

    @Override
    public Object getCollectionModelObject(Object rawInfo) throws Exception {
        TableRowData info = new TableRowData();
        StudyCountInfo studyCountInfo = (StudyCountInfo) rawInfo;
        info.study =
            studyCountInfo;
        info.studyShortName = info.study.getStudy().getNameShort();
        info.patientCount = studyCountInfo.getPatientCount();
        info.visitCount = studyCountInfo.getCollectionEventCount();
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }

    @Override
    public StudyCountInfo getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        return ((TableRowData) item.o).study;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }
}
