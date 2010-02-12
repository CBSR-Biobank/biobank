package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.SampleStorageWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class SampleStorageInfoTable extends
    InfoTableWidget<SampleStorageWrapper> {

    private static final String[] HEADINGS = new String[] { "Sample type",
        "Volume (ml)", "Quantity" };

    private static final int[] BOUNDS = new int[] { 300, 130, 100, -1, -1, -1,
        -1 };

    public SampleStorageInfoTable(Composite parent,
        Collection<SampleStorageWrapper> sampleStorageCollection) {
        super(parent, sampleStorageCollection, HEADINGS, BOUNDS);
    }

    @Override
    public List<SampleStorageWrapper> getCollection() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BiobankLabelProvider getLabelProvider() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SampleStorageWrapper getSelection() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        // TODO Auto-generated method stub
        return null;
    }
}
