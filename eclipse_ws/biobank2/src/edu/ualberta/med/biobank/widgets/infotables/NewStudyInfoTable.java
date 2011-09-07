package edu.ualberta.med.biobank.widgets.infotables;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.formatters.NumberFormatter;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.helpers.SiteQuery;
import edu.ualberta.med.biobank.gui.common.widgets.AbstractInfoTableWidget;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.gui.common.widgets.BgcTableSorter;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class NewStudyInfoTable extends AbstractInfoTableWidget {

    protected static class RowData extends RowItem {
        String name;
        String nameShort;
        String status;
        Long patientCount;
        Long ceventCount;

        public RowData(Object[] props) {
            name = (String) props[0];
            nameShort = (String) props[1];
            status = (String) props[2];
            patientCount = (Long) props[3];
            ceventCount = (Long) props[4];
        }

        @Override
        public String toString() {
            return StringUtils.join(new String[] { name, nameShort, status,
                (patientCount != null) ? patientCount.toString() : "", //$NON-NLS-1$
                (ceventCount != null) ? ceventCount.toString() : "" }, "\t"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    private static final String[] HEADINGS = new String[] {
        Messages.StudyInfoTable_name_label,
        Messages.StudyInfoTable_nameshort_label,
        Messages.StudyInfoTable_status_label,
        Messages.StudyInfoTable_patients_label,
        Messages.StudyInfoTable_visits_label };

    private SiteWrapper site;

    public NewStudyInfoTable(Composite parent, SiteWrapper site)
        throws ApplicationException {
        super(parent, HEADINGS, new int[] { 100, 100, 100, 100, 100 }, 10);
        this.site = site;
        getTableViewer().setInput(SiteQuery.getSiteStudyQuickInfo(site));
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                RowData info = new RowData((Object[]) element);
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
                    return NumberFormatter.format(info.ceventCount);
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
                RowData row1 = new RowData((Object[]) e1);
                RowData row2 = new RowData((Object[]) e2);

                switch (propertyIndex) {
                case 0:
                    rc = row1.name.compareTo(row2.name);
                    break;
                case 1:
                    rc = row1.nameShort.compareTo(row2.nameShort);
                    break;
                case 2:
                    rc = row1.status.compareTo(row2.status);
                    break;
                case 3:
                    rc = row1.patientCount.compareTo(row2.patientCount);
                    break;
                case 4:
                    rc = row1.ceventCount.compareTo(row2.ceventCount);
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
        getTableViewer().setInput(SiteQuery.getSiteStudyQuickInfo(site));
    }

}
