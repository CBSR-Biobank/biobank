package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class SampleTypeInfoTable extends InfoTableWidget<SampleTypeWrapper> {

    private static final int PAGE_SIZE_ROWS = 10;

    private static final String[] HEADINGS = new String[] { "Sample Type",
        "Short Name" };

    public SampleTypeInfoTable(Composite parent,
        List<SampleTypeWrapper> sampleTypeCollection) {
        super(parent, sampleTypeCollection, HEADINGS, PAGE_SIZE_ROWS);
    }

    @Override
    protected BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                SampleTypeWrapper item = (SampleTypeWrapper) ((BiobankCollectionModel) element).o;
                if (item == null) {
                    if (columnIndex == 0) {
                        return "loading...";
                    }
                    return "";
                }
                switch (columnIndex) {
                case 0:
                    return item.getName();
                case 1:
                    return item.getNameShort();
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
        SampleTypeWrapper type = (SampleTypeWrapper) o;
        return type.getName() + "\t" + type.getNameShort();
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }

}
