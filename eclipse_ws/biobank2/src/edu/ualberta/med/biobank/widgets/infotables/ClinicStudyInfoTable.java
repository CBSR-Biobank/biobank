package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.info.StudyCountInfo;
import edu.ualberta.med.biobank.common.formatters.NumberFormatter;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;

public class ClinicStudyInfoTable extends InfoTableWidget<StudyCountInfo> {

    private static class TableRowData {
        public StudyWrapper study;
        public String studyShortName;
        public Long patientCount;
        public Long visitCount;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { studyShortName,
                (patientCount != null) ? patientCount.toString() : "", //$NON-NLS-1$
                (visitCount != null) ? visitCount.toString() : "" }, "\t"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    private static final String[] HEADINGS = new String[] {
        Messages.ClinicStudyInfoTable_study_label,
        Messages.ClinicStudyInfoTable_patient_count_label,
        Messages.ClinicStudyInfoTable_cvent_count_label };

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
                        return Messages.infotable_loading_msg;
                    }
                    return ""; //$NON-NLS-1$
                }

                switch (columnIndex) {
                case 0:
                    return item.studyShortName;
                case 1:
                    return NumberFormatter.format(item.patientCount);
                case 2:
                    return NumberFormatter.format(item.visitCount);
                default:
                    return ""; //$NON-NLS-1$
                }
            }
        };
    }

    @Override
    public Object getCollectionModelObject(Object rawInfo) throws Exception {
        TableRowData info = new TableRowData();
        StudyCountInfo studyCountInfo = (StudyCountInfo) rawInfo;
        info.study =
            new StudyWrapper(SessionManager.getAppService(),
                studyCountInfo.getStudy());
        info.studyShortName = info.study.getNameShort();
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
