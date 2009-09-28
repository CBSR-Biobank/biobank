package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.model.PatientVisit;

public class PatientVisitInfoTable extends InfoTableWidget<PatientVisit> {

    private static final String[] HEADINGS = new String[] { "Visit Number",
        "Num Samples" };

    private static final int[] BOUNDS = new int[] { 200, 130, -1, -1, -1, -1,
        -1 };

    public PatientVisitInfoTable(Composite parent,
        Collection<PatientVisit> collection) {
        super(parent, collection, HEADINGS, BOUNDS);
    }

}
