package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.model.ContainerType;

public class ContainerTypeInfoTable extends InfoTableWidget<ContainerType> {

    private static final String[] HEADINGS = new String[] { "Name", "Status",
        "Default Temperature" };

    private static final int[] BOUNDS = new int[] { 200, 130, 130, -1, -1, -1,
        -1 };

    public ContainerTypeInfoTable(Composite parent,
        Collection<ContainerType> collection) {
        super(parent, collection, HEADINGS, BOUNDS);
    }

}
