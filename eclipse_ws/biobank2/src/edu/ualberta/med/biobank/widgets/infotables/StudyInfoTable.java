package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class StudyInfoTable extends InfoTableWidget<StudyWrapper> {

    private static final String[] HEADINGS = new String[] { "Name",
        "Short Name", "Num. Patients" };

    private static final int[] BOUNDS = new int[] { 200, 130, 130, -1, -1, -1,
        -1 };

    public StudyInfoTable(Composite parent, Collection<StudyWrapper> collection) {
        super(parent, collection, HEADINGS, BOUNDS);
    }

}
