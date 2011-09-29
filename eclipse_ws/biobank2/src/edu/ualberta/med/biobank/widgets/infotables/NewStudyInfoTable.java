package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.action.site.GetSiteInfoAction.StudyInfo;
import edu.ualberta.med.biobank.common.formatters.NumberFormatter;
import edu.ualberta.med.biobank.gui.common.widgets.AbstractInfoTableWidget;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.gui.common.widgets.BgcTableSorter;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class NewStudyInfoTable extends AbstractInfoTableWidget {
    private static final String[] HEADINGS = new String[] {
        Messages.StudyInfoTable_name_label,
        Messages.StudyInfoTable_nameshort_label,
        Messages.StudyInfoTable_status_label,
        Messages.StudyInfoTable_patients_label,
        Messages.StudyInfoTable_visits_label };

    public NewStudyInfoTable(Composite parent, List<StudyInfo> studies) {
        super(parent, HEADINGS, new int[] { 100, 100, 100, 100, 100 }, 10);
        getTableViewer().setInput(studies);
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                StudyInfo info = (StudyInfo) element;

                switch (columnIndex) {
                case 0:
                    return info.study.getName();
                case 1:
                    return info.study.getNameShort();
                case 2:
                    return (info.study.getActivityStatus() != null) ? info.study
                        .getActivityStatus().getName() : ""; //$NON-NLS-1$
                case 3:
                    return NumberFormatter.format(info.patientCount);
                case 4:
                    return NumberFormatter.format(info.collectionEventCount);
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
                StudyInfo row1 = (StudyInfo) e1;
                StudyInfo row2 = (StudyInfo) e2;

                switch (propertyIndex) {
                case 0:
                    rc = row1.study.getName().compareTo(row2.study.getName());
                    break;
                case 1:
                    rc = row1.study.getNameShort().compareTo(
                        row2.study.getNameShort());
                    break;
                case 2:
                    rc = row1.study.getActivityStatus().getName()
                        .compareTo(row2.study.getActivityStatus().getName());
                    break;
                case 3:
                    rc = row1.patientCount.compareTo(row2.patientCount);
                    break;
                case 4:
                    rc = row1.collectionEventCount
                        .compareTo(row2.collectionEventCount);
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

    public void setCollection(List<?> object) {
        getTableViewer().setInput(object);
    }
}
