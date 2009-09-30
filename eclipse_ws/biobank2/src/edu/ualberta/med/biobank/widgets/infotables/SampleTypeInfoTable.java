package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.model.SampleType;

public class SampleTypeInfoTable extends InfoTableWidget<SampleType> {

    private static final String[] HEADINGS = new String[] { "Sample Type",
        "Short Name" };

    private static final int[] BOUNDS = new int[] { 300, 130, -1, -1, -1, -1,
        -1 };

    public SampleTypeInfoTable(Composite parent,
        Collection<SampleType> sampleTypeCollection) {
        super(parent, sampleTypeCollection, HEADINGS, BOUNDS);
    }
}
