package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.formatters.NumberFormatter;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.AbstractInfoTableWidget;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.gui.common.widgets.BgcTableSorter;

public class NewStudyInfoTable extends AbstractInfoTableWidget {

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
                (patientCount != null) ? patientCount.toString() : "", //$NON-NLS-1$
                (visitCount != null) ? visitCount.toString() : "" }, "\t"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    private static final String[] HEADINGS = new String[] {
        Messages.StudyInfoTable_name_label,
        Messages.StudyInfoTable_nameshort_label,
        Messages.StudyInfoTable_status_label,
        Messages.StudyInfoTable_patients_label,
        Messages.StudyInfoTable_visits_label };

    public NewStudyInfoTable(Composite parent, SiteWrapper site) {
        super(parent, HEADINGS, new int[] {}, 10);
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData info = (TableRowData) ((BiobankCollectionModel) element).o;
                if (info == null) {
                    if (columnIndex == 0) {
                        return Messages.StudyInfoTable_loading;
                    }
                    return ""; //$NON-NLS-1$
                }
                switch (columnIndex) {
                case 0:
                    return info.name;
                case 1:
                    return info.nameShort;
                case 2:
                    return (info.status != null) ? info.status : ""; //$NON-NLS-1$
                case 3:
                    return NumberFormatter.format(info.patientCount);
                case 4:
                    return NumberFormatter.format(info.visitCount);
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
                TableRowData row1 = (TableRowData) e1;
                TableRowData row2 = (TableRowData) e2;
                int rc = 0;

                switch (propertyIndex) {
                case 0:
                    rc = row1.name.compareTo(row2.name);
                    break;
                case 1:
                    rc = row1.nameShort.compareTo(row2.nameShort);
                    break;
                }
                return rc;
            }

        };
    }

    @Override
    public void firstPage() {
        // TODO Auto-generated method stub

    }

    @Override
    public void prevPage() {
        // TODO Auto-generated method stub

    }

    @Override
    public void nextPage() {
        // TODO Auto-generated method stub

    }

    @Override
    public void lastPage() {
        // TODO Auto-generated method stub

    }

    @Override
    protected boolean isEditMode() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setCollection(List<?> collection) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void init(List<?> collection) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void setPaginationParams(List<?> collection) {
        // TODO Auto-generated method stub

    }

}
