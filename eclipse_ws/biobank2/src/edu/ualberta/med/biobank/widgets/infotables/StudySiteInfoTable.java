package edu.ualberta.med.biobank.widgets.infotables;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class StudySiteInfoTable extends InfoTableWidget<SiteWrapper> {

    protected class TableRowData {
        SiteWrapper site;
        String siteNameShort;
        Long patientCount;
        Long visitCount;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { siteNameShort,
                (patientCount != null) ? patientCount.toString() : "",
                (visitCount != null) ? visitCount.toString() : "" }, "\t");

        }
    }

    private static final String[] HEADINGS = new String[] { "Study",
        "#Patients", "#Patient Visits" };

    private StudyWrapper study;

    public StudySiteInfoTable(Composite parent, StudyWrapper study) {
        super(parent, null, HEADINGS, 10);
        this.study = study;
        setCollection(study.getSiteCollection(true));
    }

    @Override
    protected BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData item = (TableRowData) ((BiobankCollectionModel) element).o;
                if (item == null) {
                    if (columnIndex == 0) {
                        return "loading...";
                    }
                    return "";
                }
                switch (columnIndex) {
                case 0:
                    return item.siteNameShort;
                case 1:
                    return (item.patientCount != null) ? item.patientCount
                        .toString() : "";
                case 2:
                    return (item.visitCount != null) ? item.visitCount
                        .toString() : "";
                default:
                    return "";
                }
            }
        };
    }

    @Override
    public TableRowData getCollectionModelObject(SiteWrapper site)
        throws Exception {
        TableRowData info = new TableRowData();
        info.siteNameShort = site.getNameShort();
        info.patientCount = study.getPatientCountForCenter(site);
        info.visitCount = site.getCollectionEventCountForStudy(study);
        return info;
    }

    @Override
    public SiteWrapper getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        TableRowData row = (TableRowData) item.o;
        Assert.isNotNull(row);
        return row.site;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }

}
