package edu.ualberta.med.biobank.forms;

import java.util.Collection;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.model.SampleSource;
import edu.ualberta.med.biobank.widgets.InfoTableWidget;

public class SampleSourceInfoTable extends InfoTableWidget<SampleSource> {

    private static final String[] headings = new String[] { "Sample vessel" };

    private static final int[] bounds = new int[] { 300, -1, -1, -1, -1, -1, -1 };

    public SampleSourceInfoTable(Composite parent,
        Collection<SampleSource> sampleStorageCollection) {
        super(parent, sampleStorageCollection, headings, bounds);
    }
}