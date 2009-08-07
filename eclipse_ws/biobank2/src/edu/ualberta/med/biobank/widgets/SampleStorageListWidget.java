package edu.ualberta.med.biobank.widgets;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.model.SampleStorage;

public class SampleStorageListWidget extends BiobankCollectionTable {

    private static final String[] headings = new String[] { "Sample type",
        "Volume", "Quantity" };

    private static final int[] bounds = new int[] { 130, 130, 100, -1, -1, -1,
        -1 };

    private Collection<SampleStorage> sampleStorageCollection;

    public SampleStorageListWidget(Composite parent,
        Collection<SampleStorage> sampleStorageCollection) {
        super(parent, SWT.NONE, headings, bounds, null);
        GridData tableData = ((GridData) getLayoutData());
        tableData.heightHint = 500;
        this.sampleStorageCollection = sampleStorageCollection;
    }

}
