package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class ActivityStatusInfoTable extends
    InfoTableWidget<ActivityStatusWrapper> {

    private static final String[] HEADINGS = new String[] { Messages.ActivityStatusInfoTable_status_label };

    public ActivityStatusInfoTable(Composite parent,
        List<ActivityStatusWrapper> activityStatusCollection) {
        super(parent, activityStatusCollection, HEADINGS, 10);
    }

    @Override
    protected BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                ActivityStatusWrapper item = (ActivityStatusWrapper) ((BiobankCollectionModel) element).o;
                if (item == null) {
                    if (columnIndex == 0) {
                        return Messages.ActivityStatusInfoTable_loading;
                    }
                    return ""; //$NON-NLS-1$
                }
                switch (columnIndex) {
                case 0:
                    return item.getName();
                default:
                    return ""; //$NON-NLS-1$
                }
            }
        };
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((ActivityStatusWrapper) o).getName();
    }

    @Override
    public ActivityStatusWrapper getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        ActivityStatusWrapper activityStatus = (ActivityStatusWrapper) item.o;
        Assert.isNotNull(activityStatus);
        return activityStatus;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }
}