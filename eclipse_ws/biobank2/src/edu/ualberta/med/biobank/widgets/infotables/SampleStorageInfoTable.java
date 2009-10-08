package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.SampleStorageWrapper;

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
}
