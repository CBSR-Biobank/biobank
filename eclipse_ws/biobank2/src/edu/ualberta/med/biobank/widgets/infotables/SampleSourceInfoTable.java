package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.SampleSourceWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class SampleSourceInfoTable extends InfoTableWidget<SampleSourceWrapper> {

    private static final String[] HEADINGS = new String[] { "Source vessel" };

    private static final int[] BOUNDS = new int[] { 300, -1, -1, -1, -1, -1, -1 };

    public SampleSourceInfoTable(Composite parent,
        Collection<SampleSourceWrapper> sampleStorageCollection) {
        super(parent, sampleStorageCollection, HEADINGS, BOUNDS);
    }

    @Override
    public List<SampleSourceWrapper> getCollection() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BiobankLabelProvider getLabelProvider() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SampleSourceWrapper getSelection() {
        // TODO Auto-generated method stub
        return null;
    }
}