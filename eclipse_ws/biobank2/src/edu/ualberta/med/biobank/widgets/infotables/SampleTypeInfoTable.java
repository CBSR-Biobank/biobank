package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.model.SampleType;

public class SampleTypeInfoTable extends InfoTableWidget<SampleType> {

    private static final String[] headings = new String[] { "Sample Type",
        "Short Name", "ID" };

    private static final int[] bounds = new int[] { 300, 130, 100, -1, -1, -1,
        -1 };

    public SampleTypeInfoTable(Composite parent,
        Collection<SampleType> sampleTypeCollection) {
        super(parent, sampleTypeCollection, headings, bounds);
    }
}
