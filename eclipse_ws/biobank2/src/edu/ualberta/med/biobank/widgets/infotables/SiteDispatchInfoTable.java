package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import edu.ualberta.med.biobank.widgets.infotables.SiteDispatchInfoTable.StudySiteDispatch;

public class SiteDispatchInfoTable extends InfoTableWidget<StudySiteDispatch> {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(SiteDispatchInfoTable.class.getName());

    public static class StudySiteDispatch {
        public StudyWrapper study;
        public SiteWrapper destSite;
    }

    protected class TableRowData {
        StudySiteDispatch studySite;
        String studyName;
        String siteName;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { studyName, siteName }, "\t");
        }
    }

    private static final String[] HEADINGS = new String[] { "Study",
        "Destination Site" };

    private static final int[] BOUNDS = new int[] { 130, 130, -1 };

    private SiteWrapper srcSite;

    public SiteDispatchInfoTable(Composite parent, SiteWrapper site) {
        super(parent, null, HEADINGS, BOUNDS, 10);
        this.srcSite = site;
        reload();
    }

    public void reload() {
        List<StudySiteDispatch> dispatchList = new ArrayList<StudySiteDispatch>();
        List<StudyWrapper> dispatchStudies = srcSite.getDispatchStudies();
        if (dispatchStudies != null) {
            for (StudyWrapper study : dispatchStudies) {
                try {
                    List<SiteWrapper> destSites = srcSite
                        .getStudyDispachSites(study);
                    for (SiteWrapper destSite : destSites) {
                        StudySiteDispatch ssd = new StudySiteDispatch();
                        ssd.study = study;
                        ssd.destSite = destSite;
                        dispatchList.add(ssd);
                    }
                } catch (Exception e) {
                    logger
                        .error(
                            "Error while retrieving dispatch sites from source site",
                            e);
                }
            }
        }
        reloadCollection(dispatchList);
    }

    @Override
    protected BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData info = (TableRowData) ((BiobankCollectionModel) element).o;
                if (info == null) {
                    if (columnIndex == 0) {
                        return "loading...";
                    }
                    return "";
                }
                switch (columnIndex) {
                case 0:
                    return info.studyName;
                case 1:
                    return info.siteName;
                default:
                    return "";
                }
            }
        };
    }

    @Override
    public Object getCollectionModelObject(StudySiteDispatch ssd)
        throws Exception {
        TableRowData info = new TableRowData();
        info.studySite = ssd;
        info.studyName = ssd.study.getNameShort();
        info.siteName = ssd.destSite.getNameShort();
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }

    @Override
    public StudySiteDispatch getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        TableRowData row = (TableRowData) item.o;
        Assert.isNotNull(row);
        return row.studySite;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }

}
