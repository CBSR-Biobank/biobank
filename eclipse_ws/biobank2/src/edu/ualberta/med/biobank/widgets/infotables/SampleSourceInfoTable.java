package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.model.SampleSource;

public class SampleSourceInfoTable extends InfoTableWidget<SampleSource> {

    private static final String[] HEADINGS = new String[] { "Source vessel" };

    private static final int[] BOUNDS = new int[] { 300, -1, -1, -1, -1, -1, -1 };

    public SampleSourceInfoTable(Composite parent,
        Collection<SampleSource> sampleStorageCollection) {
        super(parent, sampleStorageCollection, HEADINGS, BOUNDS);
    }
}