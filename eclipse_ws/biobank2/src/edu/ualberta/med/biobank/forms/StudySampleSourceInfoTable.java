package edu.ualberta.med.biobank.forms;

import java.util.Collection;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.model.StudySampleSource;
import edu.ualberta.med.biobank.widgets.InfoTableWidget;

public class StudySampleSourceInfoTable extends
    InfoTableWidget<StudySampleSource> {

    private static final String[] headings = new String[] { "Sample vessel",
        "Quantity" };

    private static final int[] bounds = new int[] { 300, 130, -1, -1, -1, -1,
        -1 };

    public StudySampleSourceInfoTable(Composite parent,
        Collection<StudySampleSource> sampleStorageCollection) {
        super(parent, sampleStorageCollection, headings, bounds);
    }
}