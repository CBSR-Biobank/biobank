package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.SampleSourceWrapper;

public class SampleSourceInfoTable extends InfoTableWidget<SampleSourceWrapper> {

    private static final String[] HEADINGS = new String[] { "Source vessel" };

    private static final int[] BOUNDS = new int[] { 300, -1, -1, -1, -1, -1, -1 };

    public SampleSourceInfoTable(Composite parent,
        Collection<SampleSourceWrapper> sampleStorageCollection) {
        super(parent, sampleStorageCollection, HEADINGS, BOUNDS);
    }
}