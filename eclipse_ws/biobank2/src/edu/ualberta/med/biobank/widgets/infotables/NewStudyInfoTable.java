package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.action.info.StudyCountInfo;
import edu.ualberta.med.biobank.common.formatters.NumberFormatter;
import edu.ualberta.med.biobank.gui.common.widgets.AbstractInfoTableWidget;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.gui.common.widgets.BgcTableSorter;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class NewStudyInfoTable extends AbstractInfoTableWidget<StudyCountInfo> {
    private static final String[] HEADINGS = new String[] {
        Messages.StudyInfoTable_name_label,
        Messages.StudyInfoTable_nameshort_label,
        Messages.StudyInfoTable_status_label,
        Messages.StudyInfoTable_patients_label,
        Messages.StudyInfoTable_visits_label };

    public NewStudyInfoTable(Composite parent, List<StudyCountInfo> studies) {
        super(parent, HEADINGS, new int[] { 100, 100, 100, 100, 100 }, 10);
        getTableViewer().setInput(studies);
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                StudyCountInfo info = (StudyCountInfo) element;

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
    protected BgcTableSorter getTableSorter() {
        return new BgcTableSorter() {

            @Override
            public int compare(Viewer viewer, Object e1, Object e2) {
                int rc = 0;
                StudyCountInfo row1 = (StudyCountInfo) e1;
                StudyCountInfo row2 = (StudyCountInfo) e2;

                switch (propertyIndex) {
                case 0:
                    rc = row1.getStudy().getName()
                        .compareTo(row2.getStudy().getName());
                    break;
                case 1:
                    rc = row1.getStudy().getNameShort()
                        .compareTo(row2.getStudy().getNameShort());
                    break;
                case 2:
                    rc = row1
                        .getStudy()
                        .getActivityStatus()
                        .getName()
                        .compareTo(
                            row2.getStudy().getActivityStatus().getName());
                    break;
                case 3:
                    rc = row1.getPatientCount().compareTo(
                        row2.getPatientCount());
                    break;
                case 4:
                    rc = row1.getCollectionEventCount().compareTo(
                        row2.getCollectionEventCount());
                    break;
                }
                // If descending order, flip the direction
                if (direction == DESCENDING) {
                    rc = -rc;
                }
                return rc;
            }

        };
    }

    @Override
    public void firstPage() {
        // all data on one page, do nothing
    }

    @Override
    public void prevPage() {
        // all data on one page, do nothing
    }

    @Override
    public void nextPage() {
        // all data on one page, do nothing
    }

    @Override
    public void lastPage() {
        // TODO Auto-generated method stub
    }

    @Override
    protected boolean isEditMode() {
        return false;
    }

    @Override
    public void reload() throws ApplicationException {
    }
}
