package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;

public class SampleTypeInfoTable extends InfoTableWidget<SampleTypeWrapper> {

    private static final String[] HEADINGS = new String[] { "Sample Type",
        "Short Name" };

    private static final int[] BOUNDS = new int[] { 300, 130, -1, -1, -1, -1,
        -1 };

    public SampleTypeInfoTable(Composite parent,
        Collection<SampleTypeWrapper> sampleTypeCollection) {
        super(parent, sampleTypeCollection, HEADINGS, BOUNDS);
    }
}
