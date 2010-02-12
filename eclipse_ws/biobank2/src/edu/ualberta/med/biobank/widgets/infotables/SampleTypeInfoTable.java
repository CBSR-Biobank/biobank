package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class SampleTypeInfoTable extends InfoTableWidget<SampleTypeWrapper> {

    private static final String[] HEADINGS = new String[] { "Sample Type",
        "Short Name" };

    private static final int[] BOUNDS = new int[] { 300, 130, -1, -1, -1, -1,
        -1 };

    public SampleTypeInfoTable(Composite parent,
        Collection<SampleTypeWrapper> sampleTypeCollection) {
        super(parent, sampleTypeCollection, HEADINGS, BOUNDS);
    }

    @Override
    public List<SampleTypeWrapper> getCollection() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BiobankLabelProvider getLabelProvider() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SampleTypeWrapper getSelection() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        // TODO Auto-generated method stub
        return null;
    }
}
