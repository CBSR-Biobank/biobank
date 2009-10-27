package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;

public class ContainerTypeInfoTable extends
    InfoTableWidget<ContainerTypeWrapper> {

    private static final String[] HEADINGS = new String[] { "Name", "Capacity",
        "Status", "In Use", "Temperature" };

    private static final int[] BOUNDS = new int[] { 160, 130, 130, 130, 130 };

    public ContainerTypeInfoTable(Composite parent,
        Collection<ContainerTypeWrapper> collection) {
        super(parent, collection, HEADINGS, BOUNDS);
    }

}
