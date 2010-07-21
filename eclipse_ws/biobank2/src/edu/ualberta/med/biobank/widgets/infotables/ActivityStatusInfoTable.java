package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class ActivityStatusInfoTable extends
    InfoTableWidget<ActivityStatusWrapper> {

    private static final String[] HEADINGS = new String[] { "Activity Status method" };

    private static final int[] BOUNDS = new int[] { 300, -1, -1, -1, -1, -1, -1 };

    public ActivityStatusInfoTable(Composite parent,
        List<ActivityStatusWrapper> activityStatusCollection) {
        super(parent, activityStatusCollection, HEADINGS, BOUNDS, 10);
    }

    @Override
    protected BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                ActivityStatusWrapper item = (ActivityStatusWrapper) ((BiobankCollectionModel) element).o;
                if (item == null) {
                    if (columnIndex == 0) {
                        return "loading...";
                    }
                    return "";
                }
                switch (columnIndex) {
                case 0:
                    return item.getName();
                default:
                    return "";
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
        ActivityStatusWrapper shipping = (ActivityStatusWrapper) item.o;
        Assert.isNotNull(shipping);
        return shipping;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }
}