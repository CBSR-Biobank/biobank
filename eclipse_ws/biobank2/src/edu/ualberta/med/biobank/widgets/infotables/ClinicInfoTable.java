package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;

public class ClinicInfoTable extends InfoTableWidget<ClinicWrapper> {

    private static final String[] HEADINGS = new String[] { "Name",
        "Num Studies" };

    private static final int[] BOUNDS = new int[] { 200, 130, -1, -1, -1, -1,
        -1 };

    public ClinicInfoTable(Composite parent,
        Collection<ClinicWrapper> collection) {
        super(parent, collection, HEADINGS, BOUNDS);
    }

}
