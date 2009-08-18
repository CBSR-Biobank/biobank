package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.model.SampleStorage;

public class SampleStorageInfoTable extends InfoTableWidget<SampleStorage> {

    private static final String[] headings = new String[] { "Sample type",
        "Volume (ml)", "Quantity" };

    private static final int[] bounds = new int[] { 300, 130, 100, -1, -1, -1,
        -1 };

    public SampleStorageInfoTable(Composite parent,
        Collection<SampleStorage> sampleStorageCollection) {
        super(parent, sampleStorageCollection, headings, bounds);
    }
}
