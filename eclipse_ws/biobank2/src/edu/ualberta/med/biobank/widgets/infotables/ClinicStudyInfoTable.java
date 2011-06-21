package edu.ualberta.med.biobank.widgets.infotables;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.formatters.NumberFormatter;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class ClinicStudyInfoTable extends InfoTableWidget<StudyWrapper> {

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

    private ClinicWrapper clinic;

    public ClinicStudyInfoTable(Composite parent, ClinicWrapper clinic) {
        super(parent, null, HEADINGS, 10);
        this.clinic = clinic;
        setCollection(clinic.getStudyCollection());
    }

    @Override
    protected BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData item = (TableRowData) ((BiobankCollectionModel) element).o;
                if (item == null) {
                    if (columnIndex == 0) {
                        return Messages.ClinicStudyInfoTable_loading;
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
    public Object getCollectionModelObject(StudyWrapper study) throws Exception {
        TableRowData info = new TableRowData();
        info.study = study;
        info.studyShortName = study.getNameShort();
        if (info.studyShortName == null) {
            info.studyShortName = ""; //$NON-NLS-1$
        }
        info.patientCount = clinic.getPatientCountForStudy(study);
        info.visitCount = clinic.getCollectionEventCountForStudy(study);
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
