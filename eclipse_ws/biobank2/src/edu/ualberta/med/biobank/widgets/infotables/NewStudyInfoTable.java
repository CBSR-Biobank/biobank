package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.info.StudyCountInfo;
import edu.ualberta.med.biobank.common.formatters.NumberFormatter;
import edu.ualberta.med.biobank.common.permission.study.StudyDeletePermission;
import edu.ualberta.med.biobank.common.permission.study.StudyUpdatePermission;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class NewStudyInfoTable extends InfoTableWidget<StudyCountInfo> {
    private static final String[] HEADINGS = new String[] {
        Messages.StudyInfoTable_name_label,
        Messages.StudyInfoTable_nameshort_label,
        Messages.StudyInfoTable_status_label,
        Messages.StudyInfoTable_patients_label,
        Messages.StudyInfoTable_visits_label };

    public NewStudyInfoTable(Composite parent, List<StudyCountInfo> studies) {
        super(parent, studies, HEADINGS, 10,
            null);
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                StudyCountInfo info =
                    (StudyCountInfo) ((BiobankCollectionModel) element).o;

                switch (columnIndex) {
                case 0:
                    return info.getStudy().getName();
                case 1:
                    return info.getStudy().getNameShort();
                case 2:
                    return (info.getStudy().getActivityStatus() != null) ? info
                        .getStudy().getActivityStatus().getName() : ""; //$NON-NLS-1$
                case 3:
                    return NumberFormatter.format(info.getPatientCount());
                case 4:
                    return NumberFormatter.format(info
                        .getCollectionEventCount());
                default:
                    return ""; //$NON-NLS-1$
                }
            }
        };
    }

    @Override
    protected boolean isEditMode() {
        return false;
    }

    @Override
    public void reload() {
    }

    @Override
    public StudyCountInfo getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null) return null;
        return (StudyCountInfo) item.o;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return new BiobankTableSorter() {
            private static final long serialVersionUID = 1L;

            @Override
            public int compare(Object o1, Object o2) {
                if (o1 instanceof StudyCountInfo
                    && o2 instanceof StudyCountInfo) {
                    StudyCountInfo p1 = (StudyCountInfo) o1;
                    StudyCountInfo p2 = (StudyCountInfo) o2;
                    return p1.getStudy().getNameShort()
                        .compareTo(p2.getStudy().getNameShort());
                }
                return super.compare(01, o2);
            }
        };
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Boolean canEdit(StudyCountInfo target)
        throws ApplicationException {
        return SessionManager.getAppService().isAllowed(
            new StudyUpdatePermission(target.getStudy().getId()));
    }

    @Override
    protected Boolean canDelete(StudyCountInfo target)
        throws ApplicationException {
        return SessionManager.getAppService().isAllowed(
            new StudyDeletePermission(target.getStudy().getId()));
    }
}
